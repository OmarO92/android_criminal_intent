package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.database.CrimeCursorWrapper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by omaroseguera on 5/4/17.
 * Crimelab is a SINGLETON:
 *  - singleton exists as long as the application stays in memory, so storing the list in a
 *    singleton will keep the crime data available throughout any lifecycle changes
 *    in activities and fragments. Singleton classes are destroyed when Android removes
 *    app from memory
 */


public class CrimeLab {
    private static CrimeLab sCrimeLab;

    //Context used to load resource, launch enw activity , create views, get system service
    private Context mContext;
    private SQLiteDatabase mDatabase;//SQLite Database

    public static CrimeLab get(Context context) {
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        //getwriteable database does:
            //open up database file or create new one if not there
            //if first time created, save out latest version #
            //if not first time, check version number for possible updates
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }


    //insert into database
    public void addCrime(Crime c){
        ContentValues values = getContentValues(c);

        //sqlitedb.insert(tablename,null,values)
        //null value is to pass in empty values if needed
        mDatabase.insert(CrimeTable.NAME,null,values);
    }

    //getCrimes loads a List of crimes from the database by using a Cursor
    public List<Crime>getCrimes() {//returns list of Crimes
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null,null);

        //movetoFirst goes to first element
        //move to next to go to next row
        //isAfterLast tells pointer that it is past end of data
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return crimes;
    }

    //returns crime by ID
    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if(cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally{
            cursor.close();
        }

    }

    public File getPhotoFile(Crime crime) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir,crime.getPhotoFilename());
    }



    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        // ? used to prevent sql injection, last argument is a where clause
        mDatabase.update(CrimeTable.NAME,values,CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }
    public void deleteCrime(UUID crimeId)
    {
        String uuidString = crimeId.toString();
        // ? used to prevent sql injection, last argument is a where clause
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[] {uuidString});
    }

    //Cursors def: choose table to use cursor on,where clause and where arguments
    //CrimeCursorWrapper.java
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
          CrimeTable.NAME,
                null,//columns - null selects all columns
                whereClause,
                whereArgs,
                null,//groupBy
                null,//having
                CrimeTable.Cols.DATE//orderBy
        );
        return new CrimeCursorWrapper(cursor);
    }

    //_id is not used b/c it is auto incremented unique row id
    //ContentValues are KeyValue pairs, where Key is column name
    //these are used to insert and update rows
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID,crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT,crime.getSuspect());

        return values;

    }






}

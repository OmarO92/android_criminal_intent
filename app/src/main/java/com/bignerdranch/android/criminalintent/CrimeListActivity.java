package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by omaroseguera on 5/5/17.
 * when app launches, list of crimes needs to be first page
 * so make this activity the launcher activity
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }
}

package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by omaroseguera on 5/4/17.
 * created FragmentActivity as Abstract class because it will be used multiple times
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);


        //To add fragment to activit, make explicit call to FragmentManager
        //get the fragment manager

        //requires import of appcompat's FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        //give fm a fragment to manage
        //find by its fragment container id
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            //create fragment transaction
            //transactions are used to add,remove,attach,detach,replace
            //fragments in the fragment list
            //the heart of how you use fragments to compose and recompose
            //screens at runtime
            //FragmentManager maintains a back stack of fragment transactions to navigate
            fm.beginTransaction()//create new fragment transaction
                    //include one add operation
                    //fragment container id is where the fragment should appear
                    //also used as unique ID for a fragment in Fragment manager's list
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

    }
}

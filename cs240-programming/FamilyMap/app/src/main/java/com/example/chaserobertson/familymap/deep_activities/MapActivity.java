package com.example.chaserobertson.familymap.deep_activities;

//import android.app.Fragment;
//import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.chaserobertson.familymap.R;
import com.example.chaserobertson.familymap.fragments.MapFragment;

public class MapActivity extends DeepActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment == null) {
            fragment = new MapFragment();
            fragment.setHasOptionsMenu(false);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}

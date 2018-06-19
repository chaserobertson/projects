package com.example.chaserobertson.familymap.main_activities;

//import android.app.Fragment;
//import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import com.amazon.geo.mapsv2.AmazonMap;
import com.amazon.geo.mapsv2.OnMapReadyCallback;
import com.amazon.geo.mapsv2.model.LatLng;
import com.amazon.geo.mapsv2.model.Marker;
import com.amazon.geo.mapsv2.model.MarkerOptions;
import com.amazon.geo.mapsv2.util.AmazonMapsRuntimeUtil;
import com.amazon.geo.mapsv2.util.ConnectionResult;
import com.example.chaserobertson.familymap.R;
import com.example.chaserobertson.familymap.deep_activities.PersonActivity;
import com.example.chaserobertson.familymap.fragments.LoginFragment;
import com.example.chaserobertson.familymap.fragments.MapFragment;
import com.example.chaserobertson.familymap.model.Event;
import com.example.chaserobertson.familymap.model.Model;
import com.example.chaserobertson.familymap.model.Person;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView nameTextView;
    TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.iconView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
//        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
//        assert linearLayout != null;
//        linearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(Model.SINGLETON.focusPerson != null) {
//                    Intent intent = new Intent(getBaseContext(), PersonActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });

        if(getIntent().getBooleanExtra("skipToMapFlag", false)) onLogin();
        else {
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);
            if (fragment == null) {
                fragment = new LoginFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            }
        }
    }

    public void onLogin() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new MapFragment();
        fragment.setHasOptionsMenu(true);
        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        imageView.setImageDrawable(null);
        nameTextView.setText("");
        descriptionTextView.setText("");
        Model.SINGLETON.focusPerson = null;
        Model.SINGLETON.focusEvent = null;

        super.onBackPressed();
    }
}

package com.example.chaserobertson.familymap.main_activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.chaserobertson.familymap.R;
import com.example.chaserobertson.familymap.model.Model;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Model.SINGLETON.focusEvent = null;
                Model.SINGLETON.focusPerson = null;
                onBackPressed();
            default:
        }
        return true;
    }
}

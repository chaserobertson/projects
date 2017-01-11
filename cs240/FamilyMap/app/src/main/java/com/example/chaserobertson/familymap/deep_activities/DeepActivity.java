package com.example.chaserobertson.familymap.deep_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.chaserobertson.familymap.R;
import com.example.chaserobertson.familymap.main_activities.MainActivity;
import com.example.chaserobertson.familymap.model.Model;

public class DeepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deep_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.upItem:
                //getActivity().onBackPressed();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("skipToMapFlag", true);
                Model.SINGLETON.focusEvent = null;
                Model.SINGLETON.focusPerson = null;
                startActivity(intent);
                break;
            case android.R.id.home:
                onBackPressed();
            default:
        }
        return true;
    }
}

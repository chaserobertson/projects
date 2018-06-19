package com.example.chaserobertson.familymap.main_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.chaserobertson.familymap.R;
import com.example.chaserobertson.familymap.fragments.LoginFragment;
import com.example.chaserobertson.familymap.model.Model;

import org.json.JSONException;

import java.util.Objects;

public class SettingsActivity extends MainMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final String[] colors = getResources().getStringArray(R.array.color_spinner);
        final String[] mapSettings = getResources().getStringArray(R.array.map_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.color_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner lifeStorySpinner = (Spinner) findViewById(R.id.lifeStorySpinner);
        lifeStorySpinner.setAdapter(adapter);
        setColorSpinner(lifeStorySpinner, Model.SINGLETON.lifeStoryLinesColor);
        //lifeStorySpinner.setSelection(0);
        lifeStorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Model.SINGLETON.lifeStoryLinesColor = colors[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        final Switch lifeStorySwitch = (Switch)findViewById(R.id.lifeStorySwitch);
        assert lifeStorySwitch != null;
        lifeStorySwitch.setChecked(Model.SINGLETON.lifeStoryLinesOn);
        lifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Model.SINGLETON.lifeStoryLinesOn = isChecked;
            }
        });

        Spinner familyTreeSpinner = (Spinner) findViewById(R.id.familyTreeSpinner);
        familyTreeSpinner.setAdapter(adapter);
        setColorSpinner(familyTreeSpinner, Model.SINGLETON.familyTreeLinesColor);
        //familyTreeSpinner.setSelection(1);
        familyTreeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Model.SINGLETON.familyTreeLinesColor = colors[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        final Switch familyTreeSwitch = (Switch)findViewById(R.id.familyTreeSwitch);
        assert familyTreeSwitch != null;
        familyTreeSwitch.setChecked(Model.SINGLETON.familyTreeLinesOn);
        familyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Model.SINGLETON.familyTreeLinesOn = isChecked;
            }
        });

        Spinner spouseSpinner = (Spinner) findViewById(R.id.spouseSpinner);
        spouseSpinner.setAdapter(adapter);
        setColorSpinner(spouseSpinner, Model.SINGLETON.spouseLinesColor);
        //spouseSpinner.setSelection(2);
        spouseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Model.SINGLETON.spouseLinesColor = colors[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        final Switch spouseSwitch = (Switch)findViewById(R.id.spouseSwitch);
        assert spouseSwitch != null;
        spouseSwitch.setChecked(Model.SINGLETON.spouseLinesOn);
        spouseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Model.SINGLETON.spouseLinesOn = isChecked;
            }
        });

        adapter = ArrayAdapter.createFromResource(this, R.array.map_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner mapSpinner = (Spinner) findViewById(R.id.mapSpinner);
        mapSpinner.setAdapter(adapter);
        setSpinner(mapSpinner);
        mapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Model.SINGLETON.mapSetting = mapSettings[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        final LinearLayout resyncData = (LinearLayout)findViewById(R.id.resyncDataLayout);
        assert resyncData != null;
        resyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(resyncData()) {
                   Toast.makeText(getBaseContext(), "Data re-synced", Toast.LENGTH_SHORT).show();
                   onBackPressed();
               }
                else {
                   Toast.makeText(getBaseContext(), "Data re-sync failed", Toast.LENGTH_SHORT).show();
               }
            }
        });

        LinearLayout logout = (LinearLayout)findViewById(R.id.logoutLayout);
        assert logout != null;
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setColorSpinner(Spinner spinner, String color) {
        int position = 0;
        if(color.equals("Green")) position = 0;
        else if(color.equals("Blue")) position = 1;
        else if(color.equals("Red")) position = 2;
        spinner.setSelection(position);
    }

    private void setSpinner(Spinner spinner) {
        String mapType = Model.SINGLETON.mapSetting;
        int position = 0;
        if(mapType.equals("Normal")) position = 0;
        else if(mapType.equals("Satellite")) position = 1;
        else if(mapType.equals("Hybrid")) position = 2;
        else if(mapType.equals("Terrain")) position = 3;
        spinner.setSelection(position);
    }

    private boolean resyncData() {
        LoginFragment fragment = new LoginFragment();

        try {
            fragment.doLogin(Model.SINGLETON.username, Model.SINGLETON.password,
                    Model.SINGLETON.serverHost, Model.SINGLETON.serverPort);
        } catch (JSONException e) {
            return false;
        }

        return Model.SINGLETON.success;
    }
}

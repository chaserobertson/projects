package com.example.chaserobertson.familymap.main_activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.chaserobertson.familymap.HttpClient;
import com.example.chaserobertson.familymap.R;
import com.example.chaserobertson.familymap.fragments.LoginFragment;
import com.example.chaserobertson.familymap.model.Event;
import com.example.chaserobertson.familymap.model.Model;
import com.example.chaserobertson.familymap.model.Person;
import com.example.chaserobertson.familymap.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new LoginFragment();

        Model.SINGLETON.clear();

        try {
            doLogin();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        finish();
    }

    private void onSuccessfulLogin(JSONObject jsonObject) throws JSONException {
        User user = new User(jsonObject);
        Model.SINGLETON.user = user;

        doImport(user);
    }

    private void onSuccessfulImport(JSONArray dataArray) throws JSONException {

        Person person = new Person();
        for(int i = 0; i < dataArray.length(); ++i) {
            JSONObject jsonPerson = dataArray.getJSONObject(i);
            person = new Person(jsonPerson);
            Model.SINGLETON.peopleMap.put(person.getPersonID(), person);
        }

        Model.SINGLETON.person = person;
        Model.SINGLETON.running = true;

        String serverHost = Model.SINGLETON.serverHost;
        String serverPort = Model.SINGLETON.serverPort;
        String authorization = Model.SINGLETON.user.getAuthorization();

        String uri = "http://" + serverHost + ":" + serverPort + "/event/";

        String[] postData = new String[3];
        postData[0] = uri;
        postData[1] = authorization;
        postData[2] = "";

        EventImportTask lt = new EventImportTask();
        lt.execute(postData);
    }

    private void doImport(User user) throws JSONException {
        String serverHost = Model.SINGLETON.serverHost;
        String serverPort = Model.SINGLETON.serverPort;
        String authorization = user.getAuthorization();

        String uri = "http://" + serverHost + ":" + serverPort + "/person/";

        String[] postData = new String[3];
        postData[0] = uri;
        postData[1] = authorization;
        postData[2] = "";

        PeopleImportTask lt = new PeopleImportTask();
        lt.execute(postData);
    }

    private void doLogin() throws JSONException {
        String username = Model.SINGLETON.username;
        String password = Model.SINGLETON.password;
        String serverHost = Model.SINGLETON.serverHost;
        String serverPort = Model.SINGLETON.serverPort;

        String uri = "http://" + serverHost + ":" + serverPort + "/user/login";

        JSONObject jso = new JSONObject();
        jso.put("username", username);
        jso.put("password", password);

        String[] postData = new String[3];
        postData[0] = uri;
        postData[1] = "";
        postData[2] = jso.toString();

        LoginTask lt = new LoginTask();
        lt.execute(postData);
    }

    public class LoginTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params) {

            HttpClient httpClient = new HttpClient();

            return httpClient.getUrlString(params[0], params[1], params[2]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                if (Model.SINGLETON.responseData != null) {
                    JSONObject jsonObject = new JSONObject(Model.SINGLETON.responseData);

                    if (jsonObject.has("message")) {
                        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    } else {
                        onSuccessfulLogin(jsonObject);
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Login failed: no connection", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class PeopleImportTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params) {

            HttpClient httpClient = new HttpClient();

            return httpClient.getUrlString(params[0], params[1], params[2]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if (Model.SINGLETON.responseData != null) {
                    JSONObject object = new JSONObject(Model.SINGLETON.responseData);

                    if (!object.has("data")) {
                        Toast.makeText(getBaseContext(), "user failed: error message", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray dataArray = object.getJSONArray("data");
                        onSuccessfulImport(dataArray);
                    }
                } else {
                    Toast.makeText(getBaseContext(), "user failed: no connection", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class EventImportTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params) {

            HttpClient httpClient = new HttpClient();

            return httpClient.getUrlString(params[0], params[1], params[2]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if (Model.SINGLETON.responseData != null) {
                    JSONObject object = new JSONObject(Model.SINGLETON.responseData);

                    if (!object.has("data")) {
                        Toast.makeText(getBaseContext(), "user failed: error message", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray dataArray = object.getJSONArray("data");

                        for(int i = 0; i < dataArray.length(); ++i) {
                            JSONObject jsonEvent = dataArray.getJSONObject(i);
                            Event event = new Event(jsonEvent);
                            Model.SINGLETON.eventMap.put(event.getEventID(), event);
                            Model.SINGLETON.addToFilters(event);
                        }

                        Model.SINGLETON.initializeFilterEventsOn();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "user failed: no connection", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

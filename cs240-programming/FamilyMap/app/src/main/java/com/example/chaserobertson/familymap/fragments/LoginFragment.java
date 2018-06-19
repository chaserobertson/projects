package com.example.chaserobertson.familymap.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chaserobertson.familymap.R;
import com.example.chaserobertson.familymap.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.chaserobertson.familymap.model.*;
import com.example.chaserobertson.familymap.main_activities.MainActivity;

public class LoginFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText serverHostEditText;
    private EditText serverPortEditText;
    public boolean main = true;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Model.SINGLETON.clear();
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = (EditText)v.findViewById(R.id.username);

        passwordEditText = (EditText)v.findViewById(R.id.password);

        serverHostEditText = (EditText)v.findViewById(R.id.serverHost);

        serverPortEditText = (EditText)v.findViewById(R.id.serverPort);

        Button loginButton = (Button)v.findViewById(R.id.button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClicked();
            }
        });

        return v;
    }

    public void onButtonClicked() {
        try {
            doLogin();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onSuccessfulLogin(JSONObject jsonObject) throws JSONException {
        Model.SINGLETON.success = true;

        User user = new User(jsonObject);
        Model.SINGLETON.user = user;

        doImport(user);
    }

    private void onSuccessfulImport(JSONArray dataArray) throws JSONException {

        for(int i = 0; i < dataArray.length(); ++i) {
            JSONObject jsonPerson = dataArray.getJSONObject(i);
            Person person = new Person(jsonPerson);
            Model.SINGLETON.peopleMap.put(person.getPersonID(), person);
            if(person.getPersonID().equals(Model.SINGLETON.user.getPersonId())) {
                Model.SINGLETON.person = person;
                Toast.makeText(getContext(), "Welcome " + person.getFullName(), Toast.LENGTH_SHORT).show();
            }
        }

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
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String serverHost = serverHostEditText.getText().toString();
        String serverPort = serverPortEditText.getText().toString();

        Model.SINGLETON.username = username;
        Model.SINGLETON.password = password;
        Model.SINGLETON.serverHost = serverHost;
        Model.SINGLETON.serverPort = serverPort;

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

    public void doLogin(String username, String password, String serverHost, String serverPort) throws JSONException {
        main = false;
        Model.SINGLETON.success = false;
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
                        if(main) Toast.makeText(getContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    } else {
                        onSuccessfulLogin(jsonObject);
                    }
                } else {
                    if(main) Toast.makeText(getContext(), "Login failed: no connection", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "user failed: error message", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray dataArray = object.getJSONArray("data");
                        onSuccessfulImport(dataArray);
                    }
                } else {
                    Toast.makeText(getContext(), "user failed: no connection", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "user failed: error message", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray dataArray = object.getJSONArray("data");

                        for(int i = 0; i < dataArray.length(); ++i) {
                            JSONObject jsonEvent = dataArray.getJSONObject(i);
                            Event event = new Event(jsonEvent);
                            Model.SINGLETON.eventMap.put(event.getEventID(), event);
                            Model.SINGLETON.addToFilters(event);
                            Model.SINGLETON.running = true;
                        }

                        Model.SINGLETON.initializeFilterEventsOn();
                        if(main) {
                            ((MainActivity) getActivity()).onLogin();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "user failed: no connection", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

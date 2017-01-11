package com.example.chaserobertson.familymap.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chaserobertson on 6/2/16.
 */
public class User {
    private String Authorization;
    private String userName;
    private String personId;

    public User(JSONObject jsonObject) {
        try {
            Authorization = jsonObject.getString("Authorization");
            userName = jsonObject.getString("userName");
            personId = jsonObject.getString("personId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAuthorization() {
        return Authorization;
    }
    public String getUserName() {
        return userName;
    }
    public String getPersonId() {
        return personId;
    }
}

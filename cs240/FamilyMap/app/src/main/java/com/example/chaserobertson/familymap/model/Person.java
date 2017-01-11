package com.example.chaserobertson.familymap.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chaserobertson on 5/30/16.
 */
public class Person extends Searchable {
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;

    public Person() {}

    public Person(JSONObject jsonObject) {
        try {
            descendant = jsonObject.getString("descendant");
            personID = jsonObject.getString("personID");
            firstName = jsonObject.getString("firstName");
            lastName = jsonObject.getString("lastName");
            gender = jsonObject.getString("gender").toLowerCase();
            if(jsonObject.has("father")) fatherID = jsonObject.getString("father");
            if(jsonObject.has("mother")) motherID = jsonObject.getString("mother");
            if(jsonObject.has("spouse")) spouseID = jsonObject.getString("spouse");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Person{" +
                firstName + '\'' +
                lastName + '\'' +
                '}';
    }

    public String getFullName() {
        return (firstName + " " + lastName);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (descendant != null ? !descendant.equals(person.descendant) : person.descendant != null)
            return false;
        if (personID != null ? !personID.equals(person.personID) : person.personID != null)
            return false;
        if (firstName != null ? !firstName.equals(person.firstName) : person.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(person.lastName) : person.lastName != null)
            return false;
        if (gender != null ? !gender.equals(person.gender) : person.gender != null) return false;
        if (fatherID != null ? !fatherID.equals(person.fatherID) : person.fatherID != null)
            return false;
        if (motherID != null ? !motherID.equals(person.motherID) : person.motherID != null)
            return false;
        return spouseID != null ? spouseID.equals(person.spouseID) : person.spouseID == null;

    }

    @Override
    public int hashCode() {
        int result = descendant != null ? descendant.hashCode() : 0;
        result = 31 * result + (personID != null ? personID.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (fatherID != null ? fatherID.hashCode() : 0);
        result = 31 * result + (motherID != null ? motherID.hashCode() : 0);
        result = 31 * result + (spouseID != null ? spouseID.hashCode() : 0);
        return result;
    }
}

package com.example.chaserobertson.familymap.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chaserobertson on 6/7/16.
 */
public class Event extends Searchable implements Comparable {
    private String eventID;
    private Double latitude;
    private Double longitude;
    private String country;
    private String city;
    private String description;
    private String year;
    private boolean hasMarker;

    public Event(JSONObject jsonObject) {
        try {
            eventID = jsonObject.getString("eventID");
            personID = jsonObject.getString("personID");
            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            country = jsonObject.getString("country");
            city = jsonObject.getString("city");
            description = jsonObject.getString("description").toLowerCase();
            year = jsonObject.getString("year");
            descendant = jsonObject.getString("descendant");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                country + '\'' +
                city + '\'' +
                description + '\'' +
                year + '\'' +
                '}';
    }

    public String getEventID() {
        return eventID;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getDescription() {
        return description;
    }

    public String getYear() {
        return year;
    }

    public boolean hasMarker() {
        return hasMarker;
    }

    public void setHasMarker(boolean markerPresent) {
        hasMarker = markerPresent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return eventID != null ? !eventID.equals(event.eventID) : event.eventID != null;
    }

    @Override
    public int hashCode() {
        int result = eventID != null ? eventID.hashCode() : 0;
        result = 31 * result + (personID != null ? personID.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (descendant != null ? descendant.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object another) {
        if(another instanceof Event) {
            int i = ((Event) another).getYear().compareTo(this.getYear());
            if (i != 0) return i;
            int j = ((Event) another).getDescription().compareTo(this.getDescription());
            if(j != 0) return j;
            else return ((Event) another).getEventID().compareTo(this.getEventID());
        }
        return 0;
    }

}

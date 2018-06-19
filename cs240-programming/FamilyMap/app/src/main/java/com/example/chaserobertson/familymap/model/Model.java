package com.example.chaserobertson.familymap.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by chaserobertson on 6/2/16.
 */
public class Model {
    public static final Model SINGLETON = new Model();

    //login/user information
    public String username;
    public String password;
    public String serverHost;
    public String serverPort;
    public boolean running = false;
    public boolean success = false;
    public String responseData;
    public User user;
    public Person person;

    //settings information
    public boolean lifeStoryLinesOn = true;
    public boolean familyTreeLinesOn = true;
    public boolean spouseLinesOn = true;
    public String lifeStoryLinesColor = "Green";
    public String familyTreeLinesColor = "Blue";
    public String spouseLinesColor = "Red";
    public String mapSetting = "Normal";

    //focus for deep activities
    public Person focusPerson;
    public Event focusEvent;

    public HashMap<String,Person> peopleMap;
    public HashMap<String,Event> eventMap;
    public List<Filter> filters;
    public HashMap<String, Boolean> filterEventOn;
    public HashMap<String, Float> filterEventHue;

    public Model() {
        //people = new ArrayList<>();
        //events = new ArrayList<>();
        peopleMap = new HashMap<>();
        eventMap = new HashMap<>();
        filters = new ArrayList<>();
        filterEventOn = new HashMap<>();
        filterEventHue = new HashMap<>();
    }

    public void addToFilters(Event event) {
        String fullDescription = "FILTER BY " + event.getDescription().toUpperCase().trim() + " EVENTS";
        Filter filter = new Filter(event.getDescription().toLowerCase().trim(), fullDescription);
        if(!filters.contains(filter)) {
            filters.add(filter);
        }
    }

    public void initializeFilterEventsOn() {
        filterEventOn.clear();
        filterEventHue.clear();

        for (int i = 0; i < filters.size(); i++) {
            Filter currentFilter = filters.get(i);
            float hue = i * (360 / filters.size());
            filterEventOn.put(currentFilter.name, true);
            filterEventHue.put(currentFilter.name, hue);
        }

        Filter filter = new Filter("Father's Side", "FILTER BY FATHER'S SIDE OF FAMILY");
        if(!filters.contains(filter)) filters.add(filter);
        filterEventOn.put(filter.name, true);
        filter = new Filter("Mother's Side", "FILTER BY MOTHER'S SIDE OF FAMILY");
        if(!filters.contains(filter)) filters.add(filter);
        filterEventOn.put(filter.name, true);

        filter = new Filter("Male", "FILTER EVENTS BASED ON GENDER");
        if(!filters.contains(filter)) filters.add(filter);
        filterEventOn.put(filter.name, true);
        filter = new Filter("Female", "FILTER EVENTS BASED ON GENDER");
        if(!filters.contains(filter)) filters.add(filter);
        filterEventOn.put(filter.name, true);

    }

    public void separateFamilies(Set<Person> fatherSide, Set<Person> motherSide) {
        String fatherID = person.getFatherID();
        String motherID = person.getMotherID();
        if(fatherID != null) {
            Person father = peopleMap.get(fatherID);
            addToFamily(father, fatherSide);
        }
        if(motherID != null) {
            Person mother = peopleMap.get(motherID);
            addToFamily(mother, motherSide);
        }
    }

    public void addToFamily(Person person, Set<Person> family) {
        family.add(person);
        String fatherID = person.getFatherID();
        String motherID = person.getMotherID();

        if(fatherID != null) {
            Person father = peopleMap.get(fatherID);
            addToFamily(father, family);
        }
        if(motherID != null) {
            Person mother = peopleMap.get(motherID);
            addToFamily(mother, family);
        }
    }

    public void clear() {
        username = null;
        password = null;
        serverHost = null;
        serverPort = null;
        running = false;
        responseData = null;
        user = null;
        person = null;
        focusPerson = null;
        focusEvent = null;
        //people.clear();
        //events.clear();
        peopleMap.clear();
        eventMap.clear();
        filters.clear();
        filterEventOn.clear();
        filterEventHue.clear();
    }

    public void hasNoMarkers() {
        for(Event event : eventMap.values()) {
            event.setHasMarker(false);
        }
    }
}

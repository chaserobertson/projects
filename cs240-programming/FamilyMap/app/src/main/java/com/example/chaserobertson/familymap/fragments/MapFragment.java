package com.example.chaserobertson.familymap.fragments;

import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazon.geo.mapsv2.model.BitmapDescriptorFactory;
import com.amazon.geo.mapsv2.model.CameraPosition;
import com.amazon.geo.mapsv2.model.LatLng;
import com.amazon.geo.mapsv2.model.Marker;
import com.amazon.geo.mapsv2.model.MarkerOptions;
import com.amazon.geo.mapsv2.model.Polyline;
import com.amazon.geo.mapsv2.model.PolylineOptions;
import com.example.chaserobertson.familymap.R;
import com.example.chaserobertson.familymap.deep_activities.PersonActivity;
import com.example.chaserobertson.familymap.main_activities.FilterActivity;
import com.example.chaserobertson.familymap.main_activities.SearchActivity;
import com.example.chaserobertson.familymap.main_activities.SettingsActivity;

import com.amazon.geo.mapsv2.*;
import com.example.chaserobertson.familymap.model.Event;
import com.example.chaserobertson.familymap.model.Model;
import com.example.chaserobertson.familymap.model.Person;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MapFragment extends android.support.v4.app.Fragment {

    private SupportMapFragment mMapFragment;
    protected AmazonMap map;
    protected List<Polyline> lines;
    protected List<Marker> markers;

    protected ImageView imageView;
    protected TextView nameTextView;
    protected TextView descriptionTextView;
    LinearLayout linearLayout;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lines = new ArrayList<>();
        markers = new ArrayList<>();
        Model.SINGLETON.initializeFilterEventsOn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        imageView = (ImageView) getActivity().findViewById(R.id.iconView);

        nameTextView = (TextView) getActivity().findViewById(R.id.nameTextView);

        descriptionTextView = (TextView) getActivity().findViewById(R.id.descriptionTextView);

        linearLayout = (LinearLayout) getActivity().findViewById(R.id.linearLayout);
        assert linearLayout != null;
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Model.SINGLETON.focusPerson != null) {
                    Intent intent = new Intent(getContext(), PersonActivity.class);
                    startActivity(intent);
                }
            }
        });

        setClickableDisplay("s", "Click on a marker", "to see event details");

        return v;
    }

    protected void setClickableDisplay(String gender, String name, String description) {
        Drawable standardIcon = new IconDrawable(getActivity(), Iconify.IconValue.fa_map_marker).
                    color(Color.DKGRAY).sizeDp(80);
        if(gender.equals("f")) {
            standardIcon = new IconDrawable(getActivity(), Iconify.IconValue.fa_female).
                    color(Color.BLUE).sizeDp(80);
        }
        else if(gender.equals("m")) {
            standardIcon = new IconDrawable(getActivity(), Iconify.IconValue.fa_male).
                    color(Color.GREEN).sizeDp(80);
        }
        imageView.setImageDrawable(standardIcon);
        nameTextView.setText(name);
        descriptionTextView.setText(description);
    }

    private void drawMarkers(AmazonMap amazonMap) {
        Set<Person> fatherSide = new HashSet<>();
        Set<Person> motherSide = new HashSet<>();

        Model.SINGLETON.separateFamilies(fatherSide, motherSide);

        for(Event currentEvent : Model.SINGLETON.eventMap.values()) {
            if(currentEvent == null) break;
            String filterType = currentEvent.getDescription().toLowerCase().trim();
            Person currentPerson = Model.SINGLETON.peopleMap.get(currentEvent.getPersonID());

            String gender = currentPerson.getGender();
            if(gender.equals("f")) gender = "Female";
            else if(gender.equals("m")) gender = "Male";

            String familySide = "";
            if(fatherSide.contains(currentPerson)) familySide = "Father's Side";
            else if(motherSide.contains(currentPerson)) familySide = "Mother's Side";
            boolean familyGo = true;
            if(Model.SINGLETON.filterEventOn.containsKey(familySide)) {
                familyGo = Model.SINGLETON.filterEventOn.get(familySide);
            }

            if(! Model.SINGLETON.filterEventOn.containsKey(familySide)) new Exception().printStackTrace();
            if(currentPerson.equals(Model.SINGLETON.person) || familyGo) {
                if (Model.SINGLETON.filterEventOn.get(gender) &&
                        Model.SINGLETON.filterEventOn.get(filterType)) {
                    LatLng point = new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude());
                    float hue = Model.SINGLETON.filterEventHue.get(filterType);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(point)
                            .icon(BitmapDescriptorFactory.defaultMarker(hue))
                            .snippet(currentEvent.getEventID());
                    Marker marker = amazonMap.addMarker(markerOptions);
                    markers.add(marker);
                    currentEvent.setHasMarker(true);
                }
            }
        }
    }

    private void drawLines(Event event) {
        if(Model.SINGLETON.lifeStoryLinesOn) {
            drawLifeStoryLines(event);
        }
        if(Model.SINGLETON.familyTreeLinesOn) {
            drawFamilyLines(event);
        }
        if(Model.SINGLETON.spouseLinesOn) {
            drawSpouseLine(event);
        }
    }

    private void drawLifeStoryLines(Event event) {
        Person person = Model.SINGLETON.peopleMap.get(event.getPersonID());

        Set<Event> lifeStoryEvents = new TreeSet<>();
        for(Event currentEvent : Model.SINGLETON.eventMap.values()) {
            if(currentEvent.getPersonID().equals(person.getPersonID()) &&
                    currentEvent.hasMarker()) {
                lifeStoryEvents.add(currentEvent);
            }
        }

        Object[] objects = lifeStoryEvents.toArray();
        LatLng[] points = new LatLng[objects.length];
        for(int i = 0; i < objects.length; ++i) {
            Event current = (Event) objects[i];
            points[i] = new LatLng(current.getLatitude(), current.getLongitude());
        }

        drawLine(points, Model.SINGLETON.lifeStoryLinesColor);
    }

    private void drawFamilyLines(Event event) {
        Person person = Model.SINGLETON.peopleMap.get(event.getPersonID());
        String fatherID = person.getFatherID();
        String motherID = person.getMotherID();

        if(fatherID != null) {
            Set<Event> events = new TreeSet<>();
            boolean continuation = true;

            for (Event currentFamilyEvent : Model.SINGLETON.eventMap.values()) {
                if (currentFamilyEvent.getPersonID().equals(fatherID) &&
                        currentFamilyEvent.hasMarker()) {
                    if(currentFamilyEvent.getDescription().equals("birth")) {
                        drawLine(event, currentFamilyEvent, Model.SINGLETON.familyTreeLinesColor);
                        continuation = false;
                        break;
                    } else events.add(currentFamilyEvent);
                }
            }
            if(continuation && !events.isEmpty()) {
                Event earliest = (Event) events.toArray()[events.size() - 1];
                drawLine(event, earliest, Model.SINGLETON.familyTreeLinesColor);
            }
        }

        if(motherID != null) {
            Set<Event> events = new TreeSet<>();

            for (Event currentFamilyEvent : Model.SINGLETON.eventMap.values()) {
                if (currentFamilyEvent.getPersonID().equals(motherID) &&
                        currentFamilyEvent.hasMarker()) {
                    if(currentFamilyEvent.getDescription().equals("birth")) {
                        drawLine(event, currentFamilyEvent, Model.SINGLETON.familyTreeLinesColor);
                        return;
                    } else events.add(currentFamilyEvent);
                }
            }
            if(!events.isEmpty()) {
                Event earliest = (Event) events.toArray()[events.size() - 1];
                drawLine(event, earliest, Model.SINGLETON.familyTreeLinesColor);
                return;
            }
        }
    }

    private void drawSpouseLine(Event event) {
        Person person = Model.SINGLETON.peopleMap.get(event.getPersonID());
        String spouseID = person.getSpouseID();
        Set<Event> events = new TreeSet<>();

        if(spouseID != null) {
            for(Event currentSpouseEvent : Model.SINGLETON.eventMap.values()) {
                if(currentSpouseEvent.getPersonID().equals(spouseID) &&
                        currentSpouseEvent.hasMarker()) {
                    if(currentSpouseEvent.getDescription().equals("birth")) {
                        drawLine(event, currentSpouseEvent, Model.SINGLETON.spouseLinesColor);
                        return;
                    } else events.add(currentSpouseEvent);
                }
            }
        }

        if(!events.isEmpty()) {
            Event earliest = (Event) events.toArray()[events.size() - 1];
            drawLine(event, earliest, Model.SINGLETON.spouseLinesColor);
        }
    }

    protected void drawLine(Event firstEvent, Event secondEvent, String color) {
        int colorInt = -1;
        if(color.equals("Red")) colorInt = Color.RED;
        else if(color.equals("Green")) colorInt = Color.GREEN;
        else if(color.equals("Blue")) colorInt = Color.BLUE;

        LatLng firstPoint = new LatLng(firstEvent.getLatitude(), firstEvent.getLongitude());
        LatLng secondPoint = new LatLng(secondEvent.getLatitude(), secondEvent.getLongitude());

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(firstPoint, secondPoint)
                .color(colorInt);
        Polyline line = map.addPolyline(polylineOptions);
        lines.add(line);
    }

    protected void drawLine(LatLng[] points, String color) {
        int colorInt = -1;
        if(color.equals("Red")) colorInt = Color.RED;
        else if(color.equals("Green")) colorInt = Color.GREEN;
        else if(color.equals("Blue")) colorInt = Color.BLUE;

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(points)
                .color(colorInt);
        Polyline line = map.addPolyline(polylineOptions);
        lines.add(line);
    }

    protected void clearLines() {
        for(Polyline line : lines) {
            line.remove();
        }
    }

    protected void clearMarkers() {
        for(Marker marker : markers) {
            marker.remove();
        }
        Model.SINGLETON.hasNoMarkers();
    }

    @Override
    public void onResume() {
        super.onResume();

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);

        mMapFragment.getMapAsync(new OnMapReadyCallback(){
            MarkerClickListener markerClickListener;

            @Override
            public void onMapReady(AmazonMap amazonMap) {
                amazonMap.clear();
                // This method is called when the map is ready. The AmazonMap
                // object provided to this method will never be null.

                // Use the map reference to set UI settings and other options.
                amazonMap.getUiSettings().setZoomControlsEnabled(true);
                amazonMap.getUiSettings().setMapToolbarEnabled(false);
                amazonMap.setTrafficEnabled(true);
                amazonMap.setMapType(getMapType());
                markerClickListener = new MarkerClickListener();
                amazonMap.setOnMarkerClickListener(markerClickListener);
                //amazonMap.setOnMapClickListener(new MapClickListener());
                map = amazonMap;
                clearLines();
                clearMarkers();

                drawMarkers(amazonMap);
                if(Model.SINGLETON.focusEvent != null) {
                    drawLines(Model.SINGLETON.focusEvent);
                    focusOn(Model.SINGLETON.focusEvent);
                }
            }

            private void focusOn(Event event) {
                for(Marker marker : markers) {
                    if(marker.getSnippet().equals(event.getEventID())) {
                        markerClickListener.onMarkerClick(marker);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 4f));
                        break;
                    }
                }
            }

            private int getMapType() {
                String mapSetting = Model.SINGLETON.mapSetting;
                if(mapSetting.equals("Normal")) return AmazonMap.MAP_TYPE_NORMAL;
                else if(mapSetting.equals("Satellite")) return AmazonMap.MAP_TYPE_SATELLITE;
                else if(mapSetting.equals("Hybrid")) return AmazonMap.MAP_TYPE_HYBRID;
                else if(mapSetting.equals("Terrain")) return AmazonMap.MAP_TYPE_TERRAIN;
                else return 0;
            }

//            class MapClickListener implements AmazonMap.OnMapClickListener {
//                @Override
//                public void onMapClick(LatLng latLng) {
//                    Model.SINGLETON.focusPerson = null;
//                    Model.SINGLETON.focusEvent = null;
//                    clearLines();
//                    setClickableDisplay("s", "Click on a marker", "to see event details");
//                }
//            }

            class MarkerClickListener implements AmazonMap.OnMarkerClickListener {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Event clickedEvent = Model.SINGLETON.eventMap.get(marker.getSnippet());
                    Person clickedPerson = Model.SINGLETON.peopleMap.get(clickedEvent.getPersonID());
                    Model.SINGLETON.focusPerson = clickedPerson;
                    Model.SINGLETON.focusEvent = clickedEvent;
                    clearLines();
                    drawLines(clickedEvent);

                    StringBuilder sb = new StringBuilder();
                    sb.append(clickedEvent.getDescription());
                    sb.append(": ");
                    sb.append(clickedEvent.getCity());
                    sb.append(", ");
                    sb.append(clickedEvent.getCountry());
                    sb.append(" (");
                    sb.append(clickedEvent.getYear());
                    sb.append(")");
                    setClickableDisplay(clickedPerson.getGender(), clickedPerson.getFullName(), sb.toString());
                    return false;
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.searchMenuItem:
                intent = new Intent(getActivity().getBaseContext(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.searchMenuFilter:
                intent = new Intent(getActivity().getBaseContext(), FilterActivity.class);
                startActivity(intent);
                break;
            case R.id.searchMenuSettings:
                intent = new Intent(getActivity().getBaseContext(), SettingsActivity.class);
                startActivity(intent);
            default:
        }
        return true;
    }
}

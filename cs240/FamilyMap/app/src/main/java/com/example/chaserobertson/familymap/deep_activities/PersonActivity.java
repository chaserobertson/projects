package com.example.chaserobertson.familymap.deep_activities;

import com.example.chaserobertson.familymap.R;
import com.example.chaserobertson.familymap.model.Event;
import com.example.chaserobertson.familymap.model.Model;
import com.example.chaserobertson.familymap.model.Person;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PersonActivity extends DeepActivity {

    Person person;
    List<Event> events;
    List<Person> people;
    private RecyclerView mEventRecyclerView;
    private EventAdapter mEventAdapter;
    private RecyclerView mPersonRecyclerView;
    private PersonAdapter mPersonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        //person = Model.SINGLETON.person;
        person = Model.SINGLETON.focusPerson;

        Set<Event> eventSet = new TreeSet<>();
        for(Event event : Model.SINGLETON.eventMap.values()) {
            if(event.getPersonID().equals(person.getPersonID())) {
                eventSet.add(event);
            }
        }
        events = new ArrayList<>();
        for(Event event : eventSet) {
            events.add(0, event);
        }

        people = new ArrayList<>();
        if(Model.SINGLETON.peopleMap.containsKey(person.getSpouseID())) {
            people.add(Model.SINGLETON.peopleMap.get(person.getSpouseID()));
        }
        if(Model.SINGLETON.peopleMap.containsKey(person.getFatherID())) {
            people.add(Model.SINGLETON.peopleMap.get(person.getFatherID()));
        }
        if(Model.SINGLETON.peopleMap.containsKey(person.getMotherID())) {
            people.add(Model.SINGLETON.peopleMap.get(person.getMotherID()));
        }

        TextView firstNameView = (TextView) findViewById(R.id.personFirstName);
        TextView lastNameView = (TextView) findViewById(R.id.personLastName);
        TextView genderView = (TextView) findViewById(R.id.personGender);

        firstNameView.setText(person.getFirstName());
        lastNameView.setText(person.getLastName());

        String gender = "Female";
        if(person.getGender().equals("m")) gender = "Male";
        genderView.setText(gender);

        mEventRecyclerView = (RecyclerView) findViewById(R.id.lifeEventsRecyclerView);
        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEventAdapter = new EventAdapter(events);
        mEventRecyclerView.setAdapter(mEventAdapter);

        mPersonRecyclerView = (RecyclerView) findViewById(R.id.familyRecyclerView);
        mPersonRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPersonAdapter = new PersonAdapter(people);
        mPersonRecyclerView.setAdapter(mPersonAdapter);
    }

    private class EventHolder extends RecyclerView.ViewHolder {

        private Event mEvent;
        private TextView mNameTextView;
        private TextView mDescriptionTextView;
        private ImageView mImageView;
        private RelativeLayout mLayout;

        public EventHolder(View itemView) {
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.eventType);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.eventDescription);
            mImageView = (ImageView) itemView.findViewById(R.id.eventImage);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.eventListItem);
            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.SINGLETON.focusEvent = mEvent;
                    Intent intent = new Intent(getBaseContext(), MapActivity.class);
                    startActivity(intent);
                }
            });
        }

        public void bindFilter(Event event) {
            Person person = Model.SINGLETON.peopleMap.get(event.getPersonID());

            mEvent = event;
            mNameTextView.setText(person.getFullName());

            String description = mEvent.getDescription() + ": " + mEvent.getCity() + ", "
                    + mEvent.getCountry() + " (" + mEvent.getYear() + ")";
            mDescriptionTextView.setText(description);

            Drawable standardIcon = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_map_marker).
                    color(Color.GRAY).sizeDp(40);
            mImageView.setImageDrawable(standardIcon);
        }
    }

    private class EventAdapter extends RecyclerView.Adapter<EventHolder> {
        private List<Event> mEvents;

        public EventAdapter(List<Event> events) {
            mEvents = events;
        }

        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
            View view = layoutInflater.inflate(R.layout.list_item_event, parent , false);
            return new EventHolder(view);
        }

        @Override
        public void onBindViewHolder(EventHolder holder, int position) {
            Event event = mEvents.get(position);
            holder.bindFilter(event);
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }
    }

    private class PersonHolder extends RecyclerView.ViewHolder {

        private Person mPerson;
        private TextView mNameTextView;
        private TextView mDescriptionTextView;
        private ImageView mImageView;
        private RelativeLayout mLayout;

        public PersonHolder(View itemView) {
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.personName);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.personDescription);
            mImageView = (ImageView) itemView.findViewById(R.id.personImage);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.personListItem);
            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.SINGLETON.focusPerson = mPerson;
                    Intent intent = new Intent(getBaseContext(), PersonActivity.class);
                    startActivity(intent);
                }
            });
        }

        public void bindFilter(Person person) {
            mPerson = person;
            mNameTextView.setText(person.getFullName());

            String description;
            Drawable standardIcon = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_male).
                    color(Color.GREEN).sizeDp(40);
            if(!person.getSpouseID().equals(Model.SINGLETON.focusPerson.getPersonID())) {
                if(person.getGender().equals("f")) {
                    description = "Mother";
                    standardIcon = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_female).
                            color(Color.BLUE).sizeDp(40);
                }
                else description = "Father";
            }
            else {
                description = "Spouse";
                if(person.getGender().equals("f")) {
                    standardIcon = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_female).
                            color(Color.BLUE).sizeDp(40);
                }
            }
            mDescriptionTextView.setText(description);

            mImageView.setImageDrawable(standardIcon);
        }
    }

    private class PersonAdapter extends RecyclerView.Adapter<PersonHolder> {
        private List<Person> mPeople;

        public PersonAdapter(List<Person> people) {
            mPeople = people;
        }

        @Override
        public PersonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
            View view = layoutInflater.inflate(R.layout.list_item_person, parent , false);
            return new PersonHolder(view);
        }

        @Override
        public void onBindViewHolder(PersonHolder holder, int position) {
            Person person = mPeople.get(position);
            holder.bindFilter(person);
        }

        @Override
        public int getItemCount() {
            return mPeople.size();
        }
    }
}

package com.example.chaserobertson.familymap.main_activities;

import android.app.SearchableInfo;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.chaserobertson.familymap.R;
import com.example.chaserobertson.familymap.deep_activities.MapActivity;
import com.example.chaserobertson.familymap.deep_activities.PersonActivity;
import com.example.chaserobertson.familymap.model.Event;
import com.example.chaserobertson.familymap.model.Filter;
import com.example.chaserobertson.familymap.model.Model;
import com.example.chaserobertson.familymap.model.Person;
import com.example.chaserobertson.familymap.model.Searchable;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SearchActivity extends MainMenuActivity {

    private RecyclerView mSearchRecyclerView;
    private SearchAdapter mAdapter;
    private List<Searchable> peopleAndEvents;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        peopleAndEvents = new ArrayList<>();

        mSearchRecyclerView = (RecyclerView) findViewById(R.id.searchRecyclerView);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new SearchAdapter(peopleAndEvents);
        mSearchRecyclerView.setAdapter(mAdapter);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFor(query);
                mAdapter = new SearchAdapter(peopleAndEvents);
                mSearchRecyclerView.setAdapter(mAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    protected void searchFor(String query) {
        peopleAndEvents.clear();
        query = query.toLowerCase().trim();
        Set<Person> fatherSide = new HashSet<>();
        Set<Person> motherSide = new HashSet<>();
        Model.SINGLETON.separateFamilies(fatherSide, motherSide);

        for(Person person : Model.SINGLETON.peopleMap.values()) {
            String gender = person.getGender();
            if(gender.equals("f")) gender = "Female";
            else if(gender.equals("m")) gender = "Male";

            if(Model.SINGLETON.filterEventOn.get(gender)) {
                if (person.toString().toLowerCase().contains(query)) {
                    if(Model.SINGLETON.filterEventOn.get("Father's Side") && fatherSide.contains(person)) {
                        peopleAndEvents.add(person);
                    }
                    else if(Model.SINGLETON.filterEventOn.get("Mother's Side") && motherSide.contains(person)) {
                        peopleAndEvents.add(person);
                    }
                }
            }
        }
        for(Event event : Model.SINGLETON.eventMap.values()) {
            Person person = Model.SINGLETON.peopleMap.get(event.getPersonID());

            String gender = person.getGender();
            if(gender.equals("f")) gender = "Female";
            else if(gender.equals("m")) gender = "Male";

            if(event.hasMarker()) {
                if (event.toString().toLowerCase().contains(query)) {
                    peopleAndEvents.add(event);
                }
            }
        }

        if(peopleAndEvents.isEmpty()) {
            peopleAndEvents.add(new Searchable());
        }
    }

    private class SearchHolder extends RecyclerView.ViewHolder {

        private TextView mTextView1;
        private TextView mTextView2;
        private ImageView mImageView;
        private Searchable mSearchable;
        private RelativeLayout mItemLayout;

        public SearchHolder(View itemView) {
            super(itemView);

            mTextView1 = (TextView) itemView.findViewById(R.id.searchItemText1);
            mTextView2 = (TextView) itemView.findViewById(R.id.searchItemText2);
            mImageView = (ImageView) itemView.findViewById(R.id.searchItemImage);
            mItemLayout = (RelativeLayout) itemView.findViewById(R.id.searchListItem);
            mItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSearchable instanceof Person) {//selected item is a person
                        Model.SINGLETON.focusPerson = ((Person) mSearchable);
                        Intent intent = new Intent(getBaseContext(), PersonActivity.class);
                        startActivity(intent);
                    }
                    else if(mSearchable instanceof Event) {
                        Model.SINGLETON.focusEvent = ((Event) mSearchable);
                        Intent intent = new Intent(getBaseContext(), MapActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

        public void bindFilter(Searchable searchable) {
            mSearchable = searchable;
            if(searchable instanceof Person) {
                Person person = (Person) searchable;

                IconDrawable iconDrawable;
                if(person.getGender().equals("f")) {
                    iconDrawable = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_female).
                            color(Color.BLUE).sizeDp(40);
                } else {
                    iconDrawable = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_male).
                            color(Color.GREEN).sizeDp(40);
                }
                mImageView.setImageDrawable(iconDrawable);

                mTextView1.setText(person.getFullName());
                mTextView2.setText("");
            }
            else if(searchable instanceof Event) {
                Event event = (Event) searchable;

                IconDrawable iconDrawable = new IconDrawable(getBaseContext(), Iconify.IconValue.fa_map_marker).
                        color(Color.GRAY).sizeDp(40);
                mImageView.setImageDrawable(iconDrawable);

                String description = event.getDescription() + ": " + event.getCity() + ", " +
                        event.getCountry() + " (" + event.getYear() + ")";
                mTextView1.setText(description);
                mTextView2.setText(Model.SINGLETON.peopleMap.get(event.getPersonID()).getFullName());
            }
            else {
                mImageView.setImageDrawable(null);
                mTextView2.setText("No matching people or events found");
                mTextView1.setText("");
            }
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {
        private List<Searchable> mSearchables;

        public SearchAdapter(List<Searchable> searchables) {
            mSearchables = searchables;
        }

        @Override
        public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
            View view = layoutInflater.inflate(R.layout.list_item_search, parent , false);
            return new SearchHolder(view);
        }

        @Override
        public void onBindViewHolder(SearchHolder holder, int position) {
            Searchable searchable = mSearchables.get(position);
            holder.bindFilter(searchable);
        }

        @Override
        public int getItemCount() {
            return mSearchables.size();
        }
    }

}

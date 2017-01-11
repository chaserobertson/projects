package com.example.chaserobertson.familymap.main_activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.chaserobertson.familymap.R;
import com.example.chaserobertson.familymap.model.Filter;
import com.example.chaserobertson.familymap.model.Model;

import java.util.List;

public class FilterActivity extends MainMenuActivity {

    private RecyclerView mFilterRecyclerView;
    private FilterAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        mFilterRecyclerView = (RecyclerView) findViewById(R.id.filterRecyclerView);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new FilterAdapter(Model.SINGLETON.filters);
        mFilterRecyclerView.setAdapter(mAdapter);
    }

    private class FilterHolder extends RecyclerView.ViewHolder {

        private TextView mNameTextView;
        private TextView mDescriptionTextView;
        private Switch mEventOnSwitch;
        private Filter mFilter;

        public FilterHolder(View itemView) {
            super(itemView);

            //frameLayout = (FrameLayout) itemView;
            mNameTextView = (TextView) itemView.findViewById(R.id.eventType);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.eventDescription);
            mEventOnSwitch = (Switch) itemView.findViewById(R.id.eventSwitch);
            mEventOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Model.SINGLETON.filterEventOn.put(mFilter.name, isChecked);
                }
            });
        }

        public void bindFilter(Filter filter) {
            mFilter = filter;
            mNameTextView.setText(filter.name.substring(0,1).toUpperCase() + filter.name.substring(1) + " Events");
            mDescriptionTextView.setText(filter.description);
            mEventOnSwitch.setChecked(Model.SINGLETON.filterEventOn.get(filter.name));
        }
    }

    private class FilterAdapter extends RecyclerView.Adapter<FilterHolder> {
        private List<Filter> mFilters;

        public FilterAdapter(List<Filter> filters) {
            mFilters = filters;
        }

        @Override
        public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
            View view = layoutInflater.inflate(R.layout.list_item_filter, parent , false);
            return new FilterHolder(view);
        }

        @Override
        public void onBindViewHolder(FilterHolder holder, int position) {
            Filter filter = mFilters.get(position);
            holder.bindFilter(filter);
        }

        @Override
        public int getItemCount() {
            return mFilters.size();
        }
    }

}

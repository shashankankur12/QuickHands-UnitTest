package com.quickhandslogistics.view.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quickhandslogistics.R;
import com.quickhandslogistics.model.ScheduledEvents;
import com.quickhandslogistics.view.fragments.ScheduleFragment;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsHolder> {

    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    protected ArrayList<ScheduledEvents> eventsList;
    private Dialog mProgressDialog;
    private ScheduleFragment eventFragment;

    public EventsAdapter(Activity mActivity, ArrayList<ScheduledEvents> eventsList, ScheduleFragment eventFragment) {
        this.mActivity = mActivity;
        mLayoutInflater = LayoutInflater.from(mActivity);
        this.eventsList = eventsList;
        this.eventFragment = eventFragment;
    }

    @NonNull
    @Override
    public EventsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.layout_item_events, viewGroup, false);
        return new EventsAdapter.EventsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsHolder eventsHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class EventsHolder extends RecyclerView.ViewHolder {

        public EventsHolder(@NonNull View itemView) {
            super(itemView);
        }

    }
}

package com.quickhandslogistics.view.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.quickhandslogistics.R;
import com.quickhandslogistics.view.activities.UnScheduleDetailActivity;

import java.util.concurrent.ThreadLocalRandom;

public class EventsUnScheduledAdapter extends RecyclerView.Adapter<EventsUnScheduledAdapter.EventsHolder> {

    private int size = 4;
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private Long time;

    public EventsUnScheduledAdapter(Activity mActivity, Long time) {
        this.mActivity = mActivity;
        mLayoutInflater = LayoutInflater.from(mActivity);
        this.time = time;

        size = ThreadLocalRandom.current().nextInt(1, 10);
    }

    @NonNull
    @Override
    public EventsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.layout_item_events, viewGroup, false);
        return new EventsUnScheduledAdapter.EventsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsHolder eventsHolder, int i) {

        eventsHolder.textTitle.setText(Html.fromHtml("<b>Building : One97 Communications Private Limited</b>"));
        eventsHolder.textCustomerName.setText(Html.fromHtml("<b>Door : </b>03"));
        eventsHolder.textSubService.setText(Html.fromHtml("<b>Work Items : </b>05"));

        if (i == 0) {
            eventsHolder.textTime.setText("09:00 AM");
        } else if (i == 1) {
            eventsHolder.textTime.setText("01:15 PM");
        } else {
            eventsHolder.textTime.setText("05:45 PM");
        }

        eventsHolder.constraintRoot.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, UnScheduleDetailActivity.class);
            intent.putExtra("time", time.longValue());
            mActivity.startActivity(intent);
            mActivity.overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out);
        });
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void updateData(long time) {
        this.time = time;
        size = ThreadLocalRandom.current().nextInt(1, 10);
        notifyDataSetChanged();
    }

    class EventsHolder extends RecyclerView.ViewHolder {

        TextView textTitle;
        TextView textCustomerName;
        TextView textSubService;
        TextView textTime;
        ConstraintLayout constraintRoot;

        public EventsHolder(@NonNull View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.text_title);
            textCustomerName = itemView.findViewById(R.id.text_customer_name);
            textSubService = itemView.findViewById(R.id.text_service_subservice);
            textTime = itemView.findViewById(R.id.text_time);
            constraintRoot = itemView.findViewById(R.id.constraint_root);
        }
    }
}

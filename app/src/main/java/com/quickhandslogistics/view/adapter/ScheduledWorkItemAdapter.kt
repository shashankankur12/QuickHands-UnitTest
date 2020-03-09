package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.view.activities.WorkItemLumpersActivity
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_layout.view.constraint_root
import kotlinx.android.synthetic.main.item_lumper_layout.view.text_lumper
import kotlinx.android.synthetic.main.layout_scheduled_work_item.view.*

class ScheduledWorkItemAdapter(private val activity: Activity, private val sameDay: Boolean) :
    Adapter<ScheduledWorkItemAdapter.WorkItemViewHolder>() {

    var faker = Faker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_scheduled_work_item, parent, false)
        return WorkItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: WorkItemViewHolder, position: Int) {
        holder.lumperText?.text = "#${faker.company?.name()}"
        if (position == 0) {
            holder.text_startDate.text = "Start Time: 08:00 AM"
        } else if (position == 1) {
            holder.text_startDate.text = "Start Time: 10:00 AM"
        } else if (position == 2) {
            holder.text_startDate.text = "Start Time: 12:00 PM"
        } else if (position == 3) {
            holder.text_startDate.text = "Start Time: 02:00 PM"
        } else if (position == 4) {
            holder.text_startDate.text = "Start Time: 04:00 PM"
        }
        holder.constraintRoot.setOnClickListener {
            val intent = Intent(activity, WorkItemLumpersActivity::class.java)
            intent.putExtra(WorkItemLumpersActivity.ARG_CAN_REPLACE, sameDay)
            activity.startActivity(intent)
            activity.overridePendingTransition(
                R.anim.anim_next_slide_in,
                R.anim.anim_next_slide_out
            )
        }
    }

    class WorkItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lumperText = view.text_lumper
        var text_startDate = view.text_startDate
        var constraintRoot = view.constraint_root
    }
}
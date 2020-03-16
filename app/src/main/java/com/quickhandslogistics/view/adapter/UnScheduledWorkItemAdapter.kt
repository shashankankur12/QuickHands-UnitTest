package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.view.activities.AddWorkItemLumpersActivity
import com.quickhandslogistics.view.activities.WorkItemLumpersActivity
import com.quickhandslogistics.view.activities.WorkItemLumpersActivity.Companion.ARG_CAN_REPLACE
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_layout.view.constraint_root
import kotlinx.android.synthetic.main.layout_unscheduled_work_item.view.*

class UnScheduledWorkItemAdapter(
    private val activity: Activity,
    private val sameDay: Boolean,
    private var lumpersCountList: ArrayList<Int>
) :
    Adapter<UnScheduledWorkItemAdapter.WorkItemViewHolder>() {

    var faker = Faker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_unscheduled_work_item, parent, false)
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

        if (sameDay) {
            holder.button_add_lumpers.visibility = View.VISIBLE
            holder.button_add_lumpers.setOnClickListener {
                val intent = Intent(activity, AddWorkItemLumpersActivity::class.java)
                intent.putExtra("position", position)
                activity.startActivityForResult(intent, 101)
                activity.overridePendingTransition(
                    R.anim.anim_next_slide_in,
                    R.anim.anim_next_slide_out
                )
            }
        } else {
            holder.button_add_lumpers.visibility = View.GONE
            holder.constraintRoot.setOnClickListener {
                val intent = Intent(activity, WorkItemLumpersActivity::class.java)
                intent.putExtra(ARG_CAN_REPLACE, false)
                activity.startActivity(intent)
                activity.overridePendingTransition(
                    R.anim.anim_next_slide_in,
                    R.anim.anim_next_slide_out
                )
            }
        }

        if (lumpersCountList[position] > 0) {
            holder.text_lumpers_count.visibility = View.VISIBLE
            holder.text_lumpers_count.text = "Lumpers: ${lumpersCountList[position]}"

            holder.button_add_lumpers.visibility = View.GONE
        } else {
            holder.text_lumpers_count.visibility = View.GONE

            if(sameDay){
                holder.button_add_lumpers.visibility = View.VISIBLE
            } else{
                holder.button_add_lumpers.visibility = View.GONE
            }
        }
    }

    fun updateCount(lumpersCountList: ArrayList<Int>) {
        this.lumpersCountList = lumpersCountList
        notifyDataSetChanged()
    }

    class WorkItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lumperText = view.text_lumper
        var text_startDate = view.text_startDate
        var text_lumpers_count = view.text_lumpers_count
        var button_add_lumpers = view.button_add_lumpers
        var constraintRoot = view.constraint_root
    }
}
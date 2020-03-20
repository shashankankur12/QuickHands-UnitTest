package com.quickhandslogistics.modified.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleDetailContract
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.layout_scheduled_work_item.view.*

class ScheduledWorkItemAdapter(
    private val context: Context,
    private var adapterItemClickListener: ScheduleDetailContract.View.OnAdapterItemClickListener,
    private val sameDay: Boolean
) :
    Adapter<ScheduledWorkItemAdapter.WorkItemViewHolder>() {

    var faker = Faker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_scheduled_work_item, parent, false)
        return WorkItemViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: WorkItemViewHolder, position: Int) {
        holder.bind()
    }

    inner class WorkItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var textViewContainerName: TextView = view.textViewContainerName
        var textViewStartTime: TextView = view.textViewStartTime

        fun bind() {
            textViewContainerName.text = "#${faker.company?.name()}"
            textViewStartTime.text =
                String.format(context.getString(R.string.start_time_container), "08:00 AM")

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onWorkItemClick(sameDay)
                }
            }
        }
    }
}
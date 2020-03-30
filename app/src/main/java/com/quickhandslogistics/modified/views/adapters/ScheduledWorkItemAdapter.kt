package com.quickhandslogistics.modified.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.data.schedule.ScheduleData
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import kotlinx.android.synthetic.main.layout_scheduled_work_item.view.*

class ScheduledWorkItemAdapter(
    private val context: Context,
    private var adapterItemClickListener: ScheduleContract.View.OnAdapterItemClickListener
) :
    Adapter<ScheduledWorkItemAdapter.WorkItemViewHolder>() {

    private var scheduledData: ArrayList<ScheduleData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_scheduled_work_item, parent, false)
        return WorkItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return scheduledData.size
    }

    override fun onBindViewHolder(holder: WorkItemViewHolder, position: Int) {
        holder.bind()
    }

    fun updateList(scheduledData: ArrayList<ScheduleData>) {
        this.scheduledData.clear()
        this.scheduledData = scheduledData
        notifyDataSetChanged()
    }

    inner class WorkItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, OnItemClickListener {

        var textViewStartTime: TextView = view.textViewStartTime
        var textViewScheduleType: TextView = view.textViewScheduleType
        var recyclerViewLumpersImagesList: RecyclerView = view.recyclerViewLumpersImagesList

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(OverlapDecoration())
            }
        }

        fun bind() {
            textViewStartTime.text =
                String.format(context.getString(R.string.start_time_container), "08:00 AM")

            recyclerViewLumpersImagesList.apply {
                val lumperImages = ArrayList<ImageData>()
                for (i in 1..5) {
                    lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
                }
                adapter = LumperImagesAdapter(lumperImages, this@WorkItemViewHolder)
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onWorkItemClick()
                }
            }
        }

        override fun onLumperImageItemClick() {
            adapterItemClickListener.onLumperImagesClick()
        }
    }
}
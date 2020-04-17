package com.quickhandslogistics.modified.views.adapters.schedule

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LumperImagesContract
import com.quickhandslogistics.modified.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_scheduled_work_item.view.*

class ScheduledWorkItemAdapter(
    private val resources: Resources,
    private val workItemTypeDisplayName: String,
    private var adapterItemClickListener: ScheduleDetailContract.View.OnAdapterItemClickListener
) :
    Adapter<ScheduledWorkItemAdapter.WorkItemViewHolder>() {

    private var workItemsList: ArrayList<WorkItemDetail> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_scheduled_work_item, parent, false)
        return WorkItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return workItemsList.size
    }

    fun getItem(position: Int): WorkItemDetail {
        return workItemsList[position]
    }

    override fun onBindViewHolder(holder: WorkItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateData(workItemsList: List<WorkItemDetail>) {
        this.workItemsList.clear()
        this.workItemsList.addAll(workItemsList)
        notifyDataSetChanged()
    }

    inner class WorkItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, LumperImagesContract.OnItemClickListener {

        private val textViewStartTime: TextView = view.textViewStartTime
        private val textViewDropItems: TextView = view.textViewDropItems
        private val circleImageArrow: CircleImageView = view.circleImageViewArrow
        private val textViewAddLumpers: TextView = view.textViewAddLumpers
        private val recyclerViewLumpersImagesList: RecyclerView = view.recyclerViewLumpersImagesList

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager =
                    LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(OverlapDecoration())
            }
        }

        fun bind(workItemDetail: WorkItemDetail) {
            textViewStartTime.text = String.format(
                resources.getString(R.string.start_time_container),
                workItemDetail.startTime
            )

            when (workItemTypeDisplayName) {
                resources.getString(R.string.string_drops) -> {
                    textViewDropItems.text = String.format(
                        resources.getString(R.string.no_of_drops),
                        workItemDetail.numberOfDrops
                    )
                }
                resources.getString(R.string.string_live_loads) -> {
                    textViewDropItems.text = String.format(
                        resources.getString(R.string.live_load_sequence),
                        workItemDetail.sequence
                    )
                }
                else -> {
                    textViewDropItems.text = String.format(
                        resources.getString(R.string.outbound_sequence),
                        workItemDetail.sequence
                    )
                }
            }

            if (workItemDetail.assignedLumpersList?.size!! > 0) {
                textViewAddLumpers.visibility = View.GONE
                circleImageArrow.visibility = View.VISIBLE
                recyclerViewLumpersImagesList.visibility = View.VISIBLE

                recyclerViewLumpersImagesList.apply {
                    adapter = LumperImagesAdapter(
                        workItemDetail.assignedLumpersList!! as ArrayList<EmployeeData>,
                        this@WorkItemViewHolder
                    )
                }
                itemView.setOnClickListener(this)
            } else {
                textViewAddLumpers.visibility = View.VISIBLE
                circleImageArrow.visibility = View.GONE
                recyclerViewLumpersImagesList.visibility = View.GONE

                textViewAddLumpers.setOnClickListener(this)
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onWorkItemClick(
                        getItem(adapterPosition),
                        workItemTypeDisplayName
                    )
                }
            }
        }

        override fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>) {
            adapterItemClickListener.onLumperImagesClick(lumpersList)
        }
    }
}
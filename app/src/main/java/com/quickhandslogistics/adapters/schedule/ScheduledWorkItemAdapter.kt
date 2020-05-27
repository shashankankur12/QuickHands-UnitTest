package com.quickhandslogistics.adapters.schedule

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.common.LumperImagesAdapter
import com.quickhandslogistics.contracts.common.LumperImagesContract
import com.quickhandslogistics.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.controls.OverlapDecoration
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ScheduleUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_scheduled_workitem.view.*

class ScheduledWorkItemAdapter(
    private val resources: Resources, private val workItemTypeDisplayName: String, private val isFutureDate: Boolean,
    private var adapterItemClickListener: ScheduleDetailContract.View.OnAdapterItemClickListener
) : Adapter<ScheduledWorkItemAdapter.ViewHolder>() {

    private var workItemsList: ArrayList<WorkItemDetail> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_scheduled_workitem, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return workItemsList.size
    }

    fun getItem(position: Int): WorkItemDetail {
        return workItemsList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, LumperImagesContract.OnItemClickListener {

        private val textViewStartTime: TextView = view.textViewStartTime
        private val textViewStatus: TextView = view.textViewStatus
        private val textViewDropItems: TextView = view.textViewDropItems
        private val recyclerViewLumpersImagesList: RecyclerView = view.recyclerViewLumpersImagesList
        private val circleImageViewArrow: CircleImageView = view.circleImageViewArrow

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(OverlapDecoration())
            }

            if (isFutureDate) {
                circleImageViewArrow.visibility = View.GONE
            } else {
                itemView.setOnClickListener(this)
            }
        }

        fun bind(workItemDetail: WorkItemDetail) {
            textViewStartTime.text = String.format(resources.getString(R.string.start_time_s), DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime))

            when (workItemTypeDisplayName) {
                resources.getString(R.string.drops) -> textViewDropItems.text = String.format(resources.getString(R.string.no_of_drops_s), workItemDetail.numberOfDrops)
                resources.getString(R.string.live_loads) -> textViewDropItems.text = String.format(resources.getString(R.string.live_load_s), workItemDetail.sequence)
                else -> textViewDropItems.text = String.format(resources.getString(R.string.out_bound_s), workItemDetail.sequence)
            }

            recyclerViewLumpersImagesList.apply {
                adapter = LumperImagesAdapter(workItemDetail.assignedLumpersList!!, this@ViewHolder)
            }

            ScheduleUtils.changeStatusUIByValue(resources, workItemDetail.status, textViewStatus)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onWorkItemClick(getItem(adapterPosition), workItemTypeDisplayName)
                }
            }
        }

        override fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>) {
            adapterItemClickListener.onLumperImagesClick(lumpersList)
        }
    }

    fun updateData(workItemsList: List<WorkItemDetail>) {
        this.workItemsList.clear()
        this.workItemsList.addAll(workItemsList)
        notifyDataSetChanged()
    }
}
package com.quickhandslogistics.modified.adapters.customerSheet

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.customerSheet.CustomerSheetContainersContract
import com.quickhandslogistics.modified.controls.ScheduleUtils
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.AppConstant
import kotlinx.android.synthetic.main.item_customer_sheet_container.view.*

class CustomerSheetContainersAdapter(
    private val resources: Resources,
    var adapterItemClickListener: CustomerSheetContainersContract.View.OnAdapterItemClickListener
) :
    RecyclerView.Adapter<CustomerSheetContainersAdapter.ViewHolder>() {

    private var workItemsList: ArrayList<WorkItemDetail> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_customer_sheet_container, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return workItemsList.size
    }

    fun getItem(position: Int): WorkItemDetail {
        return workItemsList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workItemDetail = getItem(position)
        holder.bind(workItemDetail)

        when (workItemDetail.status) {
            AppConstant.WORK_ITEM_STATUS_SCHEDULED -> {
                holder.textViewStatus.text = resources.getString(R.string.scheduled)
                holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
                holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_scheduled)
            }
            AppConstant.WORK_ITEM_STATUS_ON_HOLD -> {
                holder.textViewStatus.text = resources.getString(R.string.on_hold)
                holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
                holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_on_hold)
            }
            AppConstant.WORK_ITEM_STATUS_CANCELLED -> {
                holder.textViewStatus.text = resources.getString(R.string.cancelled)
                holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
                holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_cancelled)
            }
            AppConstant.WORK_ITEM_STATUS_IN_PROGRESS -> {
                holder.textViewStatus.text = resources.getString(R.string.in_progress)
                holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_in_progress)
                holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_in_progress)
            }
            else -> {
                holder.textViewStatus.text = resources.getString(R.string.completed)
                holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
                holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_completed)
            }
        }
    }

    fun updateList(workItemsList: ArrayList<WorkItemDetail>) {
        this.workItemsList.clear()
        this.workItemsList = workItemsList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var textViewWorkItemType: TextView = itemView.textViewWorkItemType
        var textViewNote: TextView = itemView.textViewNote
        var textViewStatus: TextView = itemView.textViewStatus
        var relativeLayoutSide: RelativeLayout = itemView.relativeLayoutSide
        var clickableViewBO: View = itemView.clickableViewBO
        var recyclerViewBO: RecyclerView = itemView.recyclerViewBO
        var linearLayoutNotes: LinearLayout = itemView.linearLayoutNotes

        init {
            recyclerViewBO.apply {
                layoutManager = LinearLayoutManager(context)
            }

            clickableViewBO.setOnClickListener(this)
            linearLayoutNotes.setOnClickListener(this)
        }

        fun bind(workItemDetail: WorkItemDetail) {
            val workItemTypeDisplayName =
                ScheduleUtils.getWorkItemTypeDisplayName(workItemDetail.workItemType, resources)
            textViewWorkItemType.text = workItemTypeDisplayName

            if (!workItemDetail.notesQHLCustomer.isNullOrEmpty() &&
                workItemDetail.notesQHLCustomer != AppConstant.NOTES_NOT_AVAILABLE
            ) {
                linearLayoutNotes.visibility = View.VISIBLE
                textViewNote.text = workItemDetail.notesQHLCustomer
            } else {
                linearLayoutNotes.visibility = View.GONE
            }

            recyclerViewBO.adapter = ContainerDetailItemAdapter(
                workItemDetail.buildingOps,
                workItemDetail.buildingDetailData?.parameters
            )
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    clickableViewBO.id -> {
                        val workItemDetail = getItem(adapterPosition)
                        adapterItemClickListener.onBOItemClick(workItemDetail)
                    }
                    linearLayoutNotes.id -> {
                        val workItemDetail = getItem(adapterPosition)
                        adapterItemClickListener.onNotesItemClick(workItemDetail.notesQHLCustomer)
                    }
                }
            }
        }
    }
}
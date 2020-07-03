package com.quickhandslogistics.adapters.customerSheet

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
import com.quickhandslogistics.contracts.customerSheet.CustomerSheetContainersContract
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ScheduleUtils
import kotlinx.android.synthetic.main.item_customer_sheet_container.view.*

class CustomerSheetContainersAdapter(
    private val resources: Resources, var adapterItemClickListener: CustomerSheetContainersContract.View.OnAdapterItemClickListener
) : RecyclerView.Adapter<CustomerSheetContainersAdapter.ViewHolder>() {

    private var workItemsList: ArrayList<WorkItemDetail> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_customer_sheet_container, viewGroup, false)
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
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val textViewWorkItemType: TextView = itemView.textViewWorkItemType
        private val textViewStartTime: TextView = itemView.textViewStartTime
        private val textViewNote: TextView = itemView.textViewNote
        private val textViewStatus: TextView = itemView.textViewStatus
        private val clickableViewBO: View = itemView.clickableViewBO
        private val relativeLayoutBO: RelativeLayout = itemView.relativeLayoutBO
        private val recyclerViewBO: RecyclerView = itemView.recyclerViewBO
        private val linearLayoutNotes: LinearLayout = itemView.linearLayoutNotes

        init {
            recyclerViewBO.apply {
                layoutManager = LinearLayoutManager(context)
            }

            clickableViewBO.setOnClickListener(this)
            linearLayoutNotes.setOnClickListener(this)
        }

        fun bind(workItemDetail: WorkItemDetail) {
            val workItemTypeDisplayName = ScheduleUtils.getWorkItemTypeDisplayName(workItemDetail.workItemType, resources)
            textViewWorkItemType.text = workItemTypeDisplayName
            textViewStartTime.text = String.format(resources.getString(R.string.start_time_s), DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime))

            if (!workItemDetail.notesQHLCustomer.isNullOrEmpty() && workItemDetail.notesQHLCustomer != AppConstant.NOTES_NOT_AVAILABLE) {
                linearLayoutNotes.visibility = View.VISIBLE
                textViewNote.text = workItemDetail.notesQHLCustomer
            } else {
                linearLayoutNotes.visibility = View.GONE
            }

            ScheduleUtils.changeStatusUIByValue(resources, workItemDetail.status, textViewStatus)

            if (workItemDetail.buildingDetailData != null && !workItemDetail.buildingDetailData?.parameters.isNullOrEmpty()) {
                relativeLayoutBO.visibility = View.VISIBLE
                recyclerViewBO.adapter = ContainerDetailItemAdapter(workItemDetail.buildingOps, workItemDetail.buildingDetailData?.parameters)
            } else {
                relativeLayoutBO.visibility = View.GONE
            }
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

    fun updateList(workItemsList: ArrayList<WorkItemDetail>) {
        this.workItemsList.clear()
        this.workItemsList = workItemsList
        notifyDataSetChanged()
    }
}
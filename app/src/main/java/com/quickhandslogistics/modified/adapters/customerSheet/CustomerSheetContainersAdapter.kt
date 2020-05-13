package com.quickhandslogistics.modified.adapters.customerSheet

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
            val workItemTypeDisplayName = ScheduleUtils.getWorkItemTypeDisplayName(workItemDetail.workItemType, resources)
            textViewWorkItemType.text = workItemTypeDisplayName

            if (!workItemDetail.notesQHLCustomer.isNullOrEmpty() && workItemDetail.notesQHLCustomer != AppConstant.NOTES_NOT_AVAILABLE) {
                linearLayoutNotes.visibility = View.VISIBLE
                textViewNote.text = workItemDetail.notesQHLCustomer
            } else {
                linearLayoutNotes.visibility = View.GONE
            }

            ScheduleUtils.showStatusTextViewByStatus(textViewStatus, workItemDetail.status, resources)

            recyclerViewBO.adapter = ContainerDetailItemAdapter(workItemDetail.buildingOps, workItemDetail.buildingDetailData?.parameters)
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
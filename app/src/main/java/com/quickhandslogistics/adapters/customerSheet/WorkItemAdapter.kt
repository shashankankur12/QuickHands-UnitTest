package com.quickhandslogistics.adapters.customerSheet

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.AppConstant
import kotlinx.android.synthetic.main.item_workitem_type.view.*

class WorkItemAdapter(val resources: Resources, val customerSheetFragment: Context) : RecyclerView.Adapter<WorkItemAdapter.CustomViewHolder>() {
    private var onGoingWorkItems = ArrayList<ArrayList<WorkItemDetail>>()
    private var itemList = ArrayList<String>()

    init {
        itemList.add(resources.getString(R.string.out_bound))
        itemList.add(resources.getString(R.string.live_load))
        itemList.add(resources.getString(R.string.drop))
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CustomViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_workitem_type, viewGroup, false)
        return CustomViewHolder(itemView)
    }

    private fun getItem(position: Int): ArrayList<WorkItemDetail> {
        return onGoingWorkItems[position]
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return onGoingWorkItems.size
    }

    fun update(dropItem: ArrayList<ArrayList<WorkItemDetail>>) {
        onGoingWorkItems.clear()
        onGoingWorkItems.addAll(dropItem)
        notifyDataSetChanged()
    }

    inner class CustomViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var textViewStatusItem: TextView = itemView.textViewStatusItem
        var textViewStatusItemCancel: TextView = itemView.textViewStatusItemCancel
        private var recyclerViewStatus: RecyclerView = itemView.recyclerViewStatus
        var recyclerViewStatusCancel: RecyclerView = itemView.recyclerViewStatusCancel

        fun bind(workItemDetail: ArrayList<WorkItemDetail>) {
            if (adapterPosition == 0) {
                textViewStatusItem.text = String.format("Complete : %s", workItemDetail.size)
                val completed = getCompletedItemArray(workItemDetail)
                if (completed.size > 0)
                    setAdapter(completed)

            } else if (adapterPosition == 1) {
                textViewStatusItem.text = String.format("Cancel : %s", workItemDetail.size)
                val cancel = getCompletedItemArray(workItemDetail)
                if (cancel.size > 0)
                    setupCancelAdapter(cancel)

            } else if (adapterPosition == 2) {
                textViewStatusItem.text = String.format("Unfinished : %s", workItemDetail.size)
                var schedule = getCompletedItemArray(workItemDetail)
                if (schedule.size > 0)
                    setAdapter(schedule)
            }else if (adapterPosition == 3) {
                textViewStatusItem.text = String.format("Not-Open : %s", workItemDetail.size)
                var schedule = getCompletedItemArray(workItemDetail)
                if (schedule.size > 0)
                    setAdapter(schedule)
            }

        }

        private fun setupCancelAdapter(cancelItemList: ArrayList<ArrayList<WorkItemDetail>>) {
            var itemCount= ArrayList<String>()
            val outboundItem = ArrayList<WorkItemDetail>()
            val liveLodeItem = ArrayList<WorkItemDetail>()
            val dropItem = ArrayList<WorkItemDetail>()


            cancelItemList.forEach {
                it.forEach { item ->
                    when {
                        item.type.equals(AppConstant.WORKSHEET_WORK_ITEM_INBOUND) -> dropItem.add(item)
                        item.type.equals(AppConstant.WORKSHEET_WORK_ITEM_OUTBOUND) -> outboundItem.add(item)
                        item.type.equals(AppConstant.WORKSHEET_WORK_ITEM_LIVE) -> liveLodeItem.add(item)
                    }

                }
            }
            itemCount.add(outboundItem.size.toString())
            itemCount.add(liveLodeItem.size.toString())
            itemCount.add(dropItem.size.toString())
            recyclerViewStatus.apply {
                layoutManager = LinearLayoutManager(customerSheetFragment, LinearLayoutManager.HORIZONTAL, false)
                adapter=ParameterAdapter(itemList)
            }
        }

        private fun setAdapter(ongoing: ArrayList<ArrayList<WorkItemDetail>>) {
            recyclerViewStatus.apply {
                layoutManager = LinearLayoutManager(customerSheetFragment, LinearLayoutManager.VERTICAL, false)
                adapter=WorkTypeAdapter(resources, customerSheetFragment, ongoing, true)
            }
        }
    }

    fun getCompletedItemArray(workItemDetail: ArrayList<WorkItemDetail>): ArrayList<ArrayList<WorkItemDetail>> {
        val outboundItem = ArrayList<WorkItemDetail>()
        val liveLodeItem = ArrayList<WorkItemDetail>()
        val dropItem = ArrayList<WorkItemDetail>()
        val itemType = ArrayList<ArrayList<WorkItemDetail>>()

        workItemDetail.forEach {
            when {
                it.type.equals(AppConstant.WORKSHEET_WORK_ITEM_INBOUND) -> dropItem.add(it)
                it.type.equals(AppConstant.WORKSHEET_WORK_ITEM_OUTBOUND) -> outboundItem.add(it)
                it.type.equals(AppConstant.WORKSHEET_WORK_ITEM_LIVE) -> liveLodeItem.add(it)
            }
        }
        itemType.add(outboundItem)
        itemType.add(liveLodeItem)
        itemType.add(dropItem)

        return itemType

    }
}
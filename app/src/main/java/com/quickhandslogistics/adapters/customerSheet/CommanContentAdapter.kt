package com.quickhandslogistics.adapters.customerSheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.data.schedule.WorkItemDetail
import kotlin.collections.ArrayList

class CommanContentAdapter(
    workItemDetail: ArrayList<WorkItemDetail>,
    parameters: ArrayList<String>
) : RecyclerView.Adapter<CommanContentAdapter.CustomeViewHolder>() {
    private var onGoingWorkItems = workItemDetail
    private val parameters: ArrayList<String> = parameters

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CustomeViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_content_recycler, viewGroup, false)
        return CustomeViewHolder(itemView)
    }

    private fun getItem(position: Int): WorkItemDetail {
        return onGoingWorkItems[position]
    }

    override fun onBindViewHolder(viewHolder: CustomeViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return onGoingWorkItems.size
    }

    fun update(mOnGoingWorkItems: ArrayList<WorkItemDetail>, parameters: ArrayList<String>?) {
        onGoingWorkItems.clear()
        onGoingWorkItems.addAll(mOnGoingWorkItems)
        parameters?.let {
//            parameters.sortWith(Comparator { value1: String, value2: String ->
//                value1.toLowerCase().compareTo(value2.toLowerCase())
//            })
            this.parameters.addAll(parameters)
        }
        notifyDataSetChanged()

    }

    inner class CustomeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var recycelrViewContentItem: RecyclerView = itemView.findViewById(R.id.recycelrViewContentItem)
        init {

            recycelrViewContentItem.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
            recycelrViewContentItem.isNestedScrollingEnabled=false

        }

        fun bind(workItemDetail: WorkItemDetail) {

                recycelrViewContentItem.adapter = BuildingOpsAdapter(workItemDetail.buildingOps, parameters)

        }



    }

}
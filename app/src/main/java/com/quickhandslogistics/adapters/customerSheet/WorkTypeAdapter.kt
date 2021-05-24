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
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils.Companion.sharedPref
import kotlinx.android.synthetic.main.item_work_type_lable.view.*

class WorkTypeAdapter(
    val resources: Resources,
    val context: Context?,
    completed: ArrayList<ArrayList<WorkItemDetail>>,
    val isNoteVisible: Boolean
) : RecyclerView.Adapter<WorkTypeAdapter.CustomViewHolder>() {
    private var onGoingWorkItems = completed

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CustomViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_work_type_lable, viewGroup, false)
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
        private var textViewLiveLode: TextView = itemView.textViewLiveLode
        private var textViewLiveLoadNote: TextView = itemView.textViewLiveLoadNote
        private var recyclerViewLiveLodeHeader: RecyclerView = itemView.recyclerViewLiveLodeHeader
        private var recyclerViewLiveLodeItem: RecyclerView = itemView.recyclerViewLiveLodeItem
        private var recyclerViewLiveLoadNote: RecyclerView = itemView.recyclerViewLiveLoadNote

        fun bind(workItemDetail: ArrayList<WorkItemDetail>) {
            val leadProfile = sharedPref.getClassObject(
                AppConstant.PREFERENCE_LEAD_PROFILE,
                LeadProfileData::class.java
            ) as LeadProfileData?
            val outboundItem = ArrayList<WorkItemDetail>()
            val liveLodeItem = ArrayList<WorkItemDetail>()
            val dropItem = ArrayList<WorkItemDetail>()

            workItemDetail.forEach {
                when {
                    it.type.equals(AppConstant.WORKSHEET_WORK_ITEM_INBOUND) -> dropItem.add(it)
                    it.type.equals(AppConstant.WORKSHEET_WORK_ITEM_OUTBOUND) -> outboundItem.add(it)
                    it.type.equals(AppConstant.WORKSHEET_WORK_ITEM_LIVE) -> liveLodeItem.add(it)
                }
            }

            when (adapterPosition) {
                0 -> {
                    if (workItemDetail.size > 0) {
                        textViewLiveLode.visibility = View.VISIBLE
                        textViewLiveLoadNote.visibility = if (isNoteVisible) View.VISIBLE else View.GONE
                        textViewLiveLode.text =
                                String.format(resources.getString(R.string.out_bound_s), outboundItem.size)
                        setAdapter(leadProfile, workItemDetail)
                    } else {
                        textViewLiveLode.visibility = View.GONE
                        textViewLiveLoadNote.visibility = View.GONE
                    }
                }
                1 -> {
                    if (workItemDetail.size > 0) {
                        textViewLiveLode.text =
                                String.format(resources.getString(R.string.live_load_s), liveLodeItem.size)
                        textViewLiveLode.visibility = View.VISIBLE
                        textViewLiveLoadNote.visibility = if (isNoteVisible) View.VISIBLE else View.GONE
                        setAdapter(leadProfile, workItemDetail)
                    } else {
                        textViewLiveLode.visibility = View.GONE
                        textViewLiveLoadNote.visibility = View.GONE
                    }
                }
                2 -> {
                    if (workItemDetail.size > 0) {
                        textViewLiveLode.visibility = View.VISIBLE
                        textViewLiveLoadNote.visibility = if (isNoteVisible) View.VISIBLE else View.GONE
                        textViewLiveLode.text =
                                String.format(resources.getString(R.string.drops_value), dropItem.size)
                        setAdapter(leadProfile, workItemDetail)
                    } else {
                        textViewLiveLode.visibility = View.GONE
                        textViewLiveLoadNote.visibility = View.GONE
                    }
                }
            }
            recyclerViewLiveLoadNote.visibility = if (isNoteVisible) View.VISIBLE else View.GONE
        }

        private fun setAdapter(
            leadProfile: LeadProfileData?,
            workItemDetail: ArrayList<WorkItemDetail>
        ) {
            var buildParams = ArrayList<String>()

            workItemDetail[0].buildingParams?.let {
                buildParams = it
            }
            recyclerViewLiveLodeHeader.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = ParameterAdapter(buildParams)
            }

            recyclerViewLiveLodeItem.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = CommanContentAdapter(workItemDetail, buildParams)
            }
            recyclerViewLiveLoadNote.apply {

                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = CustomerNoteAdaptet(
                    workItemDetail,
                    buildParams
                )
            }
        }
    }
}
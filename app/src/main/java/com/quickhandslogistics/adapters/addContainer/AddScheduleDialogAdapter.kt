package com.quickhandslogistics.adapters.addContainer

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.data.addContainer.ContainerDetails
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_INBOUND
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ValueUtils
import java.util.ArrayList

class AddScheduleDialogAdapter(var mScheduleList: ArrayList<ContainerDetails>, val activity: Activity) :
    RecyclerView.Adapter<AddScheduleDialogAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_contect_list_view, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, i: Int) {
        viewHolder.bind(getItem(i))
    }

    override fun getItemCount(): Int {
        return mScheduleList.size
    }

    private fun getItem(position: Int): ContainerDetails {
        return mScheduleList[position]
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mContentText: TextView = itemView.findViewById(R.id.content_item_text)

        fun bind(containerDetails: ContainerDetails) {
            var scheduleType=when {
                containerDetails.workItemType.equals(AppConstant.WORKSHEET_WORK_ITEM_OUTBOUND) -> activity.getString(R.string.out_bound)
                containerDetails.workItemType.equals(AppConstant.WORKSHEET_WORK_ITEM_LIVE) -> activity.getString(R.string.live_load)
                containerDetails.workItemType.equals(AppConstant.WORKSHEET_WORK_ITEM_INBOUND) -> activity.getString(R.string.drop)
                else -> {""}
            }

            var quantity= if (containerDetails.workItemType.equals(WORKSHEET_WORK_ITEM_INBOUND)) ValueUtils.getDefaultOrValue(containerDetails.numberOfDrops) else ValueUtils.getDefaultOrValue(containerDetails.sequence)
            var startTime= DateUtils.convertMillisecondsToTimeString(containerDetails.startTime?.toLong()!!)

            mContentText.text=" $quantity $scheduleType at $startTime"
            itemView.setPadding(0,5,0,5)
        }

    }

}
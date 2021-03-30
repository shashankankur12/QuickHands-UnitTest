package com.quickhandslogistics.adapters.customerSheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.AppConstant
import kotlin.collections.ArrayList

class CustomerNoteAdaptet(
    workItemDetail: ArrayList<WorkItemDetail>,
    parameters: ArrayList<String>
) : RecyclerView.Adapter<CustomerNoteAdaptet.CustomeViewHolder>() {
    private var onGoingWorkItems = workItemDetail
    private val parameters: ArrayList<String> = parameters


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CustomeViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_content_note, viewGroup, false)
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

    fun update(dropItem: ArrayList<WorkItemDetail>, parameters: ArrayList<String>?) {
        onGoingWorkItems.clear()
        onGoingWorkItems.addAll(dropItem)
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
        var textViewItemDoor: TextView = itemView.findViewById(R.id.textViewItemDoor)
        var textViewItemContainer: TextView = itemView.findViewById(R.id.textViewItemContainer)
        var textViewItemContainerNote: TextView = itemView.findViewById(R.id.textViewItemContainerNote)

        fun bind(workItemDetail: WorkItemDetail) {
            if (!workItemDetail.notesQHL.isNullOrEmpty()){

                textViewItemContainerNote.text=workItemDetail.notesQHL
                textViewItemDoor.text=if(!workItemDetail.buildingOps.isNullOrEmpty()) workItemDetail.buildingOps!![parameters[0]] else AppConstant.NOTES_NOT_AVAILABLE
                textViewItemContainer.text= if(!workItemDetail.buildingOps.isNullOrEmpty())workItemDetail.buildingOps!![parameters[1]] else AppConstant.NOTES_NOT_AVAILABLE

            }

        }
    }

}
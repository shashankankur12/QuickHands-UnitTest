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

class WorkTypeAdpter(
    val resources: Resources,
    val context: Context?,
    completed: ArrayList<ArrayList<WorkItemDetail>>
) : RecyclerView.Adapter<WorkTypeAdpter.CustomeViewHolder>()
{
    private var onGoingWorkItems = completed


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CustomeViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_work_type_lable, viewGroup, false)
        return CustomeViewHolder(itemView)
    }

    private fun getItem(position: Int): ArrayList<WorkItemDetail> {
        return onGoingWorkItems[position]
    }

    override fun onBindViewHolder(viewHolder: CustomeViewHolder, position: Int) {
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

    inner class CustomeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var textViewLiveLode: TextView = itemView.findViewById(R.id.textViewLiveLode)
        private var textViewLiveLoadNote: TextView = itemView.findViewById(R.id.textViewLiveLoadNote)
        private var recycelrViewLiveLodeHeader: RecyclerView = itemView.findViewById(R.id.recycelrViewLiveLodeHeader)
        private var recycelrViewLiveLodeItem: RecyclerView = itemView.findViewById(R.id.recycelrViewLiveLodeItem)
        private var recycelrViewLiveLoadNote: RecyclerView = itemView.findViewById(R.id.recycelrViewLiveLoadNote)

        fun bind(workItemDetail: ArrayList<WorkItemDetail>) {
            val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
            if(adapterPosition.equals(0)){

                if (workItemDetail.size>0){
                    textViewLiveLode.visibility=View.VISIBLE
                    textViewLiveLoadNote.visibility=View.VISIBLE
                    textViewLiveLode.text=resources.getString(R.string.out_bounds)
                    setAdaptet( leadProfile, workItemDetail)
                }else{
                    textViewLiveLode.visibility=View.GONE
                    textViewLiveLoadNote.visibility=View.GONE
                }

            }else if(adapterPosition.equals(1)){
                if (workItemDetail.size>0){
                    textViewLiveLode.text=resources.getString(R.string.live_loads)
                    textViewLiveLode.visibility=View.VISIBLE
                    textViewLiveLoadNote.visibility=View.VISIBLE
                    setAdaptet( leadProfile, workItemDetail)
                }else{
                    textViewLiveLode.visibility=View.GONE
                    textViewLiveLoadNote.visibility=View.GONE
                }
            }else if (adapterPosition.equals(2)){
                if (workItemDetail.size>0){
                    textViewLiveLode.visibility=View.VISIBLE
                    textViewLiveLoadNote.visibility=View.VISIBLE
                    textViewLiveLode.text=resources.getString(R.string.drops)
                    setAdaptet( leadProfile, workItemDetail)
                }else{
                    textViewLiveLode.visibility=View.GONE
                    textViewLiveLoadNote.visibility=View.GONE
                }
            }

        }

        private fun setAdaptet(leadProfile: LeadProfileData?, workItemDetail: ArrayList<WorkItemDetail>) {
            var perameterAdapter = PerameterAdapter(leadProfile?.buildingDetailData?.parameters!!)
            val layoutManager1 = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recycelrViewLiveLodeHeader.layoutManager=layoutManager1
            recycelrViewLiveLodeHeader.adapter=perameterAdapter

            var dropContentAdapter= CommanContentAdapter(workItemDetail,leadProfile?.buildingDetailData?.parameters!!)
            val layoutManager3 = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recycelrViewLiveLodeItem.layoutManager=layoutManager3
            recycelrViewLiveLodeItem.adapter=dropContentAdapter

            var outBoundContentNoteAdapter= CustomerNoteAdaptet(
                workItemDetail,
                leadProfile?.buildingDetailData?.parameters!!
            )
            val layoutManager4 = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recycelrViewLiveLoadNote.layoutManager=layoutManager4
            recycelrViewLiveLoadNote.adapter=outBoundContentNoteAdapter

        }
    }


}
package com.quickhandslogistics.adapters.reports;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.reports.CustomerReportContract
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.UIUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_add_lumpers.view.*
import kotlinx.android.synthetic.main.item_schedule.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CustomerJobReportAdapter(private val onAdapterClick: CustomerReportContract.View.OnAdapterItemClickListener) : RecyclerView.Adapter<CustomerJobReportAdapter.ViewHolder>() {

         private var buildingDetailDataList: ArrayList<BuildingDetailData> = ArrayList()


         override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
             val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_add_building, parent, false)
             return ViewHolder(view, parent.context)
         }

         override fun getItemCount(): Int {
             return buildingDetailDataList.size
         }

         private fun getItem(position: Int): BuildingDetailData {
             return buildingDetailDataList[position]
         }

         override fun onBindViewHolder(holder: ViewHolder, position: Int) {
             holder.bind(getItem(position))
         }

         inner class ViewHolder(view: View, private val context: Context) :
             RecyclerView.ViewHolder(view), View.OnClickListener {

             private val textViewBuildingName: TextView = view.textViewBuildingName
             private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
             private val imageViewAdd: ImageView = view.imageViewAdd

             fun bind(buildingDetailData: BuildingDetailData) {
                 UIUtils.showEmployeeProfileImage(context, "", circleImageViewProfile)
                 textViewBuildingName.text = buildingDetailData.buildingName?.capitalize()
                 imageViewAdd.setImageResource(R.drawable.ic_check_blue)

                 itemView.setOnClickListener(this)
             }

             override fun onClick(view: View?) {
                 view?.let {
                     when (view.id) {
                         itemView.id -> {
                         }
                     }
                 }
             }
         }

         fun updateLumpersData(buildingDetailDataList: ArrayList<BuildingDetailData>) {
             this.buildingDetailDataList.clear()
             this.buildingDetailDataList.addAll(buildingDetailDataList)
             notifyDataSetChanged()
         }

         fun clearAllSelection() {
             onAdapterClick.onLumperSelectionChanged()
             notifyDataSetChanged()
         }


}



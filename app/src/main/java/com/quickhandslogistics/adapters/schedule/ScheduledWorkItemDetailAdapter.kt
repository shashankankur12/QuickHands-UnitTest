package com.quickhandslogistics.adapters.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.controls.CustomTextView
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.UIUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_workitem_detail_lumper.view.*

class ScheduledWorkItemDetailAdapter : Adapter<ScheduledWorkItemDetailAdapter.ViewHolder>() {

    private val lumpersList: ArrayList<EmployeeData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_workitem_detail_lumper, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return lumpersList.size
    }

    private fun getItem(position: Int): EmployeeData {
        return lumpersList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view) {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val textViewEmployeeId: CustomTextView = view.textViewEmployeeId
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val viewAttendanceStatus: View = view.viewAttendanceStatus

        fun bind(employeeData: EmployeeData) {
            UIUtils.showEmployeeProfileImage(context, employeeData.profileImageUrl, circleImageViewProfile)
            UIUtils.updateProfileBorder(context, employeeData.isTemporaryAssigned, circleImageViewProfile)
            textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)
            textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(employeeData)
//            attendanceDetail.isPresent?.let {
//                viewAttendanceStatus.setBackgroundResource(if (employeeData.isPresent!!) R.drawable.online_dot else R.drawable.offline_dot)
//            }
        }
    }

    fun updateData(
        lumpersList: List<EmployeeData>,
        attendanceDetail: AttendanceDetail
    ) {
        this.lumpersList.clear()
        this.lumpersList.addAll(lumpersList)
        notifyDataSetChanged()
    }
}
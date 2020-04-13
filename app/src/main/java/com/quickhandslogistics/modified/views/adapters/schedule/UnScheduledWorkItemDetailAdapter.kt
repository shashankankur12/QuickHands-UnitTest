package com.quickhandslogistics.modified.views.adapters.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_lumper_layout.view.*

class UnScheduledWorkItemDetailAdapter :
    Adapter<UnScheduledWorkItemDetailAdapter.WorkItemHolder>() {

    private val lumpersList: ArrayList<EmployeeData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lumper_layout, parent, false)
        return WorkItemHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return lumpersList.size
    }

    private fun getItem(position: Int): EmployeeData {
        return lumpersList[position]
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateData(lumpersList: List<EmployeeData>) {
        this.lumpersList.clear()
        this.lumpersList.addAll(lumpersList)
        notifyDataSetChanged()
    }

    inner class WorkItemHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val textViewEmployeeId: TextView = view.textViewEmployeeId
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val textViewShiftHours: TextView = view.textViewShiftHours
        private val imageViewCall: ImageView = view.imageViewCall

        init {
            imageViewCall.visibility = View.GONE
        }

        fun bind(employeeData: EmployeeData) {
            if (!StringUtils.isNullOrEmpty(employeeData.profileImageUrl)) {
                Glide.with(context).load(employeeData.profileImageUrl)
                    .placeholder(R.drawable.dummy).error(R.drawable.dummy)
                    .into(circleImageViewProfile)
            } else {
                Glide.with(context).clear(circleImageViewProfile);
            }

            textViewLumperName.text = String.format(
                "%s %s",
                ValueUtils.getDefaultOrValue(employeeData.firstName).capitalize(),
                ValueUtils.getDefaultOrValue(employeeData.lastName).capitalize()
            )

            if (StringUtils.isNullOrEmpty(employeeData.employeeId)) {
                textViewEmployeeId.visibility = View.GONE
            } else {
                textViewEmployeeId.visibility = View.VISIBLE
                textViewEmployeeId.text = String.format("(Emp ID: %s)", employeeData.employeeId)
            }

            if (StringUtils.isNullOrEmpty(employeeData.shiftHours)) {
                textViewShiftHours.visibility = View.GONE
            } else {
                textViewShiftHours.visibility = View.VISIBLE
                textViewShiftHours.text =
                    String.format("(Shift Hours: %s)", employeeData.shiftHours)
            }
        }
    }
}
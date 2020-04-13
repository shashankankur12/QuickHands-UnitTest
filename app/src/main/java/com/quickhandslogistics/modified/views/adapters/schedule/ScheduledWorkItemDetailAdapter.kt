package com.quickhandslogistics.modified.views.adapters.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.WorkItemDetailContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_work_item_detail_lumper.view.*

class ScheduledWorkItemDetailAdapter(
    private val onAdapterClick: WorkItemDetailContract.View.OnAdapterItemClickListener,
    private val allowUpdate: Boolean
) : Adapter<ScheduledWorkItemDetailAdapter.WorkItemHolder>() {

    private val lumpersList: ArrayList<EmployeeData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_work_item_detail_lumper, parent, false)
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
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val textViewEmployeeId: TextView = view.textViewEmployeeId
        private val viewAttendanceStatus: View = view.viewAttendanceStatus
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val buttonReplace: Button = view.buttonReplace
        private val textViewReplaced: TextView = view.textViewReplaced
        private val linearLayoutLumperTime: LinearLayout = view.linearLayoutLumperTime

        init {
            buttonReplace.setOnClickListener(this)
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

            if (adapterPosition == 0) {
                linearLayoutLumperTime.visibility = View.GONE
                buttonReplace.visibility = View.VISIBLE
                textViewReplaced.visibility = View.GONE
                viewAttendanceStatus.setBackgroundResource(R.drawable.offline_dot)
            } else {
                linearLayoutLumperTime.visibility = View.VISIBLE
                buttonReplace.visibility = View.GONE
                textViewReplaced.visibility = View.GONE
                viewAttendanceStatus.setBackgroundResource(R.drawable.online_dot)
            }

//            if (adapterPosition == replacedPosition) {
//                linearLayoutLumperTime.visibility = View.GONE
//                buttonReplace.visibility = View.GONE
//                textViewReplaced.visibility = View.VISIBLE
//            } else {
//                textViewReplaced.visibility = View.GONE
//                if (allowUpdate) {
//                    if (position == 1 || position == 3) {
//                        linearLayoutLumperTime.visibility = View.GONE
//                        buttonReplace.visibility = View.VISIBLE
//                    } else {
//                        linearLayoutLumperTime.visibility = View.VISIBLE
//                        buttonReplace.visibility = View.GONE
//                    }
//                } else {
//                    buttonReplace.visibility = View.GONE
//                    if (position == 1 || position == 3) {
//                        linearLayoutLumperTime.visibility = View.GONE
//                    } else {
//                        linearLayoutLumperTime.visibility = View.VISIBLE
//                    }
//                }
//            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    buttonReplace.id -> onAdapterClick.onReplaceItemClick(adapterPosition)
                }
            }
        }
    }
}
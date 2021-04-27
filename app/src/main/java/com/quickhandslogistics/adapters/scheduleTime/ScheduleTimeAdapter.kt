package com.quickhandslogistics.adapters.scheduleTime

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.controls.CustomTextView
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.UIUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_lumper_sheet_layout.view.*
import kotlinx.android.synthetic.main.item_schedule_time.view.*
import kotlinx.android.synthetic.main.item_schedule_time.view.circleImageViewProfile
import kotlinx.android.synthetic.main.item_schedule_time.view.textViewEmployeeId
import kotlinx.android.synthetic.main.item_schedule_time.view.textViewLumperName
import kotlinx.android.synthetic.main.item_schedule_time.view.viewAttendanceStatus
import java.util.*
import kotlin.collections.ArrayList

class ScheduleTimeAdapter(private val onAdapterClick: ScheduleTimeContract.View.OnAdapterItemClickListener) : Adapter<ScheduleTimeAdapter.ViewHolder>() {

    private var searchEnabled = false
    private var isPastDate: Boolean = false
    private var searchTerm = ""
    private var notes: String? =null

    private var tempLumperIds: ArrayList<String> = ArrayList()
    private var items: ArrayList<ScheduleTimeDetail> = ArrayList()
    private var filteredItems: ArrayList<ScheduleTimeDetail> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_time, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredItems.size else items.size
    }

    private fun getItem(position: Int): ScheduleTimeDetail {
        return if (searchEnabled) filteredItems[position] else items[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val textViewEmployeeId: CustomTextView = view.textViewEmployeeId
        private val textViewScheduleTime: TextView = view.textViewScheduleTime
        private val textViewScheduleNote: TextView = view.textViewScheduleNote
        private val textViewLumperCancel: TextView = view.textViewLumperCancel
        private val textViewScheduleEdit: TextView = view.textViewScheduleEdit
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val layoutCancelEdit: LinearLayout = view.layoutCancelEdit
        private val viewAttendanceStatus: View = view.viewAttendanceStatus

        fun bind(scheduleTimeDetail: ScheduleTimeDetail) {
            val employeeData = scheduleTimeDetail.lumperInfo
            employeeData?.let {
                UIUtils.showEmployeeProfileImage(context, employeeData.profileImageUrl, circleImageViewProfile)
                UIUtils.updateProfileBorder(context, employeeData.isTemporaryAssigned, circleImageViewProfile)
                textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)
                textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(employeeData)
                viewAttendanceStatus.setBackgroundResource(if (scheduleTimeDetail.isPresent!!) R.drawable.online_dot else R.drawable.offline_dot)
                layoutCancelEdit.visibility = if (isPastDate) View.GONE else View.VISIBLE
                textViewScheduleNote.isEnabled = !(notes.isNullOrEmpty() && (scheduleTimeDetail.notesForLumper.isNullOrEmpty() || scheduleTimeDetail.notesForLumper.equals("NA")))

            }

            textViewScheduleTime.text = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, scheduleTimeDetail.reportingTimeAndDay)
            textViewScheduleNote.setOnClickListener(this)
            textViewLumperCancel.setOnClickListener(this)
            textViewScheduleEdit.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    textViewScheduleEdit.id -> {
                        onAdapterClick.onEditTimeClick(adapterPosition, 0,getItem(adapterPosition) )
                    }
                    textViewLumperCancel.id -> {
                        onAdapterClick.onAddRemoveClick(adapterPosition, getItem(adapterPosition))
                    }
                    textViewScheduleNote.id -> {
                        onAdapterClick.onScheduleNoteClick(adapterPosition, notes, getItem(adapterPosition))
                    }
                }
            }
        }
    }

    fun isSearchEnabled(): Boolean {
        return searchEnabled
    }

    fun setSearchEnabled(enabled: Boolean, searchTerm: String = "") {
        this.searchEnabled = enabled
        if (!searchEnabled) {
            this.searchTerm = ""
            filteredItems.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filter()
    }


    private fun filter() {
        filteredItems.clear()
        if (searchTerm.isEmpty()) {
            filteredItems.addAll(items)
        } else {
            for (data in items) {
                val term = "${data.lumperInfo?.firstName} ${data.lumperInfo?.lastName}"
                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredItems.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun updateLumpersData(
        scheduleTimeDetailList: ArrayList<ScheduleTimeDetail>,
        tempLumperIds: ArrayList<String>,
        note: String?,
        pastDate: Boolean
    ) {
        items.clear()
        items.addAll(scheduleTimeDetailList)
        notes= note
        isPastDate=pastDate

        this.tempLumperIds.clear()
        this.tempLumperIds.addAll(tempLumperIds)
        notifyDataSetChanged()
    }
}
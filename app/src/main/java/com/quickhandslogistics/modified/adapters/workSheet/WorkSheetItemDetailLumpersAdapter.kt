package com.quickhandslogistics.modified.adapters.workSheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailLumpersContract
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils.getDefaultOrValue
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_work_item_detail_lumper_time.view.*

class WorkSheetItemDetailLumpersAdapter(
    private var onAdapterClick: WorkSheetItemDetailLumpersContract.View.OnAdapterItemClickListener
) : Adapter<WorkSheetItemDetailLumpersAdapter.WorkItemHolder>() {

    private var lumperList: ArrayList<EmployeeData> = ArrayList()
    private var updateData: HashMap<String, AttendanceDetail> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_work_item_detail_lumper_time, parent, false)
        return WorkItemHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return lumperList.size
    }

    private fun getItem(position: Int): EmployeeData {
        return lumperList[position]
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WorkItemHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val textViewEmployeeId: TextView = view.textViewEmployeeId
        private val textViewAddTime: TextView = view.textViewAddTime
        private val linearLayoutLumperTime: LinearLayout = view.linearLayoutLumperTime
        private val textViewShiftTime: TextView = view.textViewShiftTime
        private val textViewBreakTime: TextView = view.textViewBreakTime

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
                getDefaultOrValue(employeeData.firstName).capitalize(),
                getDefaultOrValue(employeeData.lastName).capitalize()
            )

            if (StringUtils.isNullOrEmpty(employeeData.employeeId)) {
                textViewEmployeeId.visibility = View.GONE
            } else {
                textViewEmployeeId.visibility = View.VISIBLE
                textViewEmployeeId.text = String.format("(Emp ID: %s)", employeeData.employeeId)
            }
            textViewAddTime.setOnClickListener(this)

        }

        private fun updateTimeUI(isPresent: Boolean, lumperId: String) {
            textViewAddTime.visibility = if (isPresent) View.VISIBLE else View.GONE
            linearLayoutLumperTime.visibility = if (isPresent) View.VISIBLE else View.GONE
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    textViewAddTime.id -> {
                        onAdapterClick.onAddTimeClick(adapterPosition)
                    }
                }
            }
        }
    }

    fun updateList(lumperList: ArrayList<EmployeeData>?) {
        this.updateData.clear()
        this.lumperList.clear()
        lumperList?.let {
            this.lumperList.addAll(lumperList)
        }
        notifyDataSetChanged()
    }

    private fun changeIsPresentRecord(lumperId: String?, isPresent: Boolean) {
        if (!StringUtils.isNullOrEmpty(lumperId) && !updateData.containsKey(lumperId)) {
            updateData[lumperId!!] = AttendanceDetail()
            updateData[lumperId]?.lumperId = lumperId
        }
        updateData[lumperId]?.isPresent = isPresent
        updateData[lumperId]?.isPresentChanged = true
    }

    private fun changeNotesRecord(lumperId: String?) {
        if (!StringUtils.isNullOrEmpty(lumperId) && !updateData.containsKey(lumperId)) {
            updateData[lumperId!!] = AttendanceDetail()
            updateData[lumperId]?.lumperId = lumperId
        }
    }

    private fun initiateUpdateRecord(lumperId: String?, isPresent: Boolean = true) {
        if (!StringUtils.isNullOrEmpty(lumperId) && !updateData.containsKey(lumperId)) {
            updateData[lumperId!!] = AttendanceDetail()
            updateData[lumperId]?.lumperId = lumperId
            updateData[lumperId]?.isPresent = isPresent
        }
    }

    /*fun updateClockInTime(itemPosition: Int, currentTime: Long) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.morningPunchIn = "${currentTime / 1000L}"
        updateData[item.id]?.isMorningPunchInChanged = true

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.morningPunchIn =
            DateUtils.getUTCDateString(DateUtils.PATTERN_API_RESPONSE, Date(currentTime))
        notifyDataSetChanged()
    }

    fun updateClockOutTime(itemPosition: Int, currentTime: Long) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.eveningPunchOut = "${currentTime / 1000L}"
        updateData[item.id]?.isEveningPunchOutChanged = true

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.eveningPunchOut =
            DateUtils.getUTCDateString(DateUtils.PATTERN_API_RESPONSE, Date(currentTime))
        notifyDataSetChanged()
    }

    fun updateLunchInTime(itemPosition: Int, currentTime: Long) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.lunchPunchIn = "${currentTime / 1000L}"
        updateData[item.id]?.isLunchPunchInChanged = true

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.lunchPunchIn =
            DateUtils.getUTCDateString(DateUtils.PATTERN_API_RESPONSE, Date(currentTime))
        notifyDataSetChanged()
    }

    fun updateLunchOutTime(itemPosition: Int, currentTime: Long) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.lunchPunchOut = "${currentTime / 1000L}"
        updateData[item.id]?.isLunchPunchOutChanged = true

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.lunchPunchOut =
            DateUtils.getUTCDateString(DateUtils.PATTERN_API_RESPONSE, Date(currentTime))
        notifyDataSetChanged()
    }

    fun getUpdatedData(): HashMap<String, AttendanceDetail> {
        return updateData
    }

    fun checkIfEditable(hasValue: Boolean, variableName: String, lumperId: String): Boolean {
        return if (updateData.containsKey(lumperId)) {
            val isChanged = when (variableName) {
                "isPresent" -> updateData[lumperId]?.isPresentChanged!!
                "isMorningPunchIn" -> updateData[lumperId]?.isMorningPunchInChanged!!
                "isEveningPunchOut" -> updateData[lumperId]?.isEveningPunchOutChanged!!
                "isLunchPunchIn" -> updateData[lumperId]?.isLunchPunchInChanged!!
                "isLunchPunchOut" -> updateData[lumperId]?.isLunchPunchOutChanged!!
                else -> false
            }
            if (isChanged) {
                // This field is edited for this lumper.
                isChanged
            } else {
                // Something is edited for this lumper but not this field.
                !hasValue
            }
        } else {
            // Nothing is edited for this lumper yet.
            !hasValue
        }
    }*/
}
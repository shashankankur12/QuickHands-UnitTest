package com.quickhandslogistics.modified.views.adapters.schedule

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.MarkAttendanceContract
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.LumperAttendanceData
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_RESPONSE
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import com.quickhandslogistics.utils.ValueUtils.getDefaultOrValue
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_recyclerview_mark_attendance.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MarkAttendanceAdapter(
    private var onAdapterClick: MarkAttendanceContract.View.OnAdapterItemClickListener
) : Adapter<MarkAttendanceAdapter.WorkItemHolder>() {

    private var searchEnabled = false
    private var searchTerm = ""
    private var lumperAttendanceList: ArrayList<LumperAttendanceData> = ArrayList()
    private var lumperAttendanceFilteredList: ArrayList<LumperAttendanceData> = ArrayList()
    private var updateData: HashMap<String, AttendanceDetail> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recyclerview_mark_attendance, parent, false)
        return WorkItemHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) lumperAttendanceFilteredList.size else lumperAttendanceList.size
    }

    private fun getItem(position: Int): LumperAttendanceData {
        return if (searchEnabled) lumperAttendanceFilteredList[position] else lumperAttendanceList[position]
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WorkItemHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener, TextWatcher {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val viewAttendanceStatus: View = view.viewAttendanceStatus
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val switchAttendance: SwitchCompat = view.switchAttendance
        private val textViewEmployeeId: TextView = view.textViewEmployeeId
        private val textViewAddTime: TextView = view.textViewAddTime
        private val textViewNoTimeLoggedIn: TextView = view.textViewNoTimeLoggedIn
        private val linearLayoutLumperTime: LinearLayout = view.linearLayoutLumperTime
        private val textViewShiftTime: TextView = view.textViewShiftTime
        private val textViewLunchTime: TextView = view.textViewLunchTime
        private val editTextNotes: EditText = view.editTextNotes

        fun bind(lumperAttendance: LumperAttendanceData) {
            if (!StringUtils.isNullOrEmpty(lumperAttendance.profileImageUrl)) {
                Glide.with(context).load(lumperAttendance.profileImageUrl)
                    .placeholder(R.drawable.dummy).error(R.drawable.dummy)
                    .into(circleImageViewProfile)
            } else {
                Glide.with(context).clear(circleImageViewProfile);
            }

            textViewLumperName.text = String.format(
                "%s %s",
                getDefaultOrValue(lumperAttendance.firstName).capitalize(),
                getDefaultOrValue(lumperAttendance.lastName).capitalize()
            )

            if (StringUtils.isNullOrEmpty(lumperAttendance.employeeId)) {
                textViewEmployeeId.visibility = View.GONE
            } else {
                textViewEmployeeId.visibility = View.VISIBLE
                textViewEmployeeId.text = String.format("(Emp ID: %s)", lumperAttendance.employeeId)
            }

            lumperAttendance.attendanceDetail?.let { attendanceDetail ->
                val isPresent = getDefaultOrValue(attendanceDetail.isPresent)
                if (isPresent) {
                    val morningPunchIn = DateUtils.convertDateStringToTime(
                        PATTERN_API_RESPONSE, attendanceDetail.morningPunchIn
                    )
                    val eveningPunchOut = DateUtils.convertDateStringToTime(
                        PATTERN_API_RESPONSE, attendanceDetail.eveningPunchOut
                    )
                    textViewShiftTime.text = String.format(
                        "%s - %s",
                        if (morningPunchIn.isNotEmpty()) morningPunchIn else "NA",
                        if (eveningPunchOut.isNotEmpty()) eveningPunchOut else "NA"
                    )

                    val lunchPunchIn = DateUtils.convertDateStringToTime(
                        PATTERN_API_RESPONSE, attendanceDetail.lunchPunchIn
                    )
                    val lunchPunchOut = DateUtils.convertDateStringToTime(
                        PATTERN_API_RESPONSE, attendanceDetail.lunchPunchOut
                    )
                    textViewLunchTime.text = String.format(
                        "%s - %s",
                        if (lunchPunchIn.isNotEmpty()) lunchPunchIn else "NA",
                        if (lunchPunchOut.isNotEmpty()) lunchPunchOut else "NA"
                    )
                }
                editTextNotes.setText(attendanceDetail.attendanceNote)
                updateTimeUI(isPresent)
            }

            textViewAddTime.setOnClickListener(this)
        }

        private fun updateTimeUI(isPresent: Boolean) {
            viewAttendanceStatus.setBackgroundResource(if (isPresent) R.drawable.online_dot else R.drawable.offline_dot)
            switchAttendance.isChecked = isPresent
            switchAttendance.isEnabled = !isPresent
            textViewAddTime.visibility = if (isPresent) View.VISIBLE else View.GONE
            linearLayoutLumperTime.visibility = if (isPresent) View.VISIBLE else View.GONE
            textViewNoTimeLoggedIn.visibility = if (isPresent) View.GONE else View.VISIBLE

            switchAttendance.setOnClickListener(this)
            editTextNotes.addTextChangedListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    textViewAddTime.id -> {
                        onAdapterClick.onAddTimeClick(getItem(adapterPosition), adapterPosition)
                    }
                    switchAttendance.id -> {
                        val item = getItem(adapterPosition)
                        // Update in API Request Object
                        initiateUpdateRecord(item.id)

                        //Update in Local List Object to show changes on UI
                        getItem(adapterPosition).attendanceDetail?.isPresent = true

                        notifyDataSetChanged()
                    }
                }
            }
        }

        override fun afterTextChanged(text: Editable?) {
            val item = getItem(adapterPosition)
            if (getDefaultOrValue(item.attendanceDetail?.attendanceNote) != text.toString()) {
                // Update in API Request Object
                initiateUpdateRecord(item.id, setIsPresent = false)
                updateData[item.id]?.attendanceNote = text.toString()

                //Update in Local List Object to show changes on UI
                getItem(adapterPosition).attendanceDetail?.attendanceNote = text.toString()
                onAdapterClick.onAddNotes(updateData.size)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    fun setSearchEnabled(enabled: Boolean, searchTerm: String = "") {
        this.searchEnabled = enabled
        if (!searchEnabled) {
            this.searchTerm = ""
            lumperAttendanceFilteredList.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filter()
    }

    private fun filter() {
        lumperAttendanceFilteredList.clear()
        if (searchTerm.isEmpty()) {
            lumperAttendanceFilteredList.addAll(lumperAttendanceList)
        } else {
            for (data in lumperAttendanceList) {
                val term = data.firstName!!
                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    lumperAttendanceFilteredList.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun updateList(lumperAttendanceList: ArrayList<LumperAttendanceData>) {
        this.updateData.clear()
        this.lumperAttendanceList.clear()
        this.lumperAttendanceFilteredList.clear()
        this.lumperAttendanceList.addAll(lumperAttendanceList)
        notifyDataSetChanged()
    }

    private fun initiateUpdateRecord(lumperId: String?, setIsPresent: Boolean = true) {
        if (!StringUtils.isNullOrEmpty(lumperId) && !updateData.containsKey(lumperId)) {
            updateData[lumperId!!] = AttendanceDetail()
            updateData[lumperId]?.lumperId = lumperId
            if (setIsPresent) {
                updateData[lumperId]?.isPresent = true
            }
        }
    }

    fun updateClockInTime(itemPosition: Int, currentTime: Long) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.morningPunchIn = "${currentTime / 1000L}"

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.morningPunchIn =
            DateUtils.getUTCDateString(PATTERN_API_RESPONSE, Date(currentTime))
        notifyDataSetChanged()
    }

    fun updateClockOutTime(itemPosition: Int, currentTime: Long) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.eveningPunchOut = "${currentTime / 1000L}"

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.eveningPunchOut =
            DateUtils.getUTCDateString(PATTERN_API_RESPONSE, Date(currentTime))
        notifyDataSetChanged()
    }

    fun updateLunchInTime(itemPosition: Int, currentTime: Long) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.lunchPunchIn = "${currentTime / 1000L}"

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.lunchPunchIn =
            DateUtils.getUTCDateString(PATTERN_API_RESPONSE, Date(currentTime))
        notifyDataSetChanged()
    }

    fun updateLunchOutTime(itemPosition: Int, currentTime: Long) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.lunchPunchOut = "${currentTime / 1000L}"

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.lunchPunchOut =
            DateUtils.getUTCDateString(PATTERN_API_RESPONSE, Date(currentTime))
        notifyDataSetChanged()
    }

    fun getUpdatedData(): HashMap<String, AttendanceDetail> {
        return updateData
    }
}
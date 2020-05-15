package com.quickhandslogistics.modified.adapters.attendance

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.SparseBooleanArray
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.util.keyIterator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.attendance.TimeClockAttendanceContract
import com.quickhandslogistics.modified.controls.CustomTextView
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.LumperAttendanceData
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_EVENING_PUNCH_OUT
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_IS_PRESENT
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_LUNCH_PUNCH_IN
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_LUNCH_PUNCH_OUT
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_MORNING_PUNCH_IN
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_RESPONSE
import com.quickhandslogistics.utils.DateUtils.Companion.convertDateStringToTime
import com.quickhandslogistics.utils.FlipAnimator
import com.quickhandslogistics.utils.UIUtils
import com.quickhandslogistics.utils.ValueUtils.getDefaultOrValue
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_time_clock_attendance.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TimeClockAttendanceAdapter(private var onAdapterClick: TimeClockAttendanceContract.View.OnAdapterItemClickListener) :
    Adapter<TimeClockAttendanceAdapter.ViewHolder>() {

    private var searchEnabled = false
    private var searchTerm = ""

    private var lumperAttendanceList: ArrayList<LumperAttendanceData> = ArrayList()
    private var lumperAttendanceFilteredList: ArrayList<LumperAttendanceData> = ArrayList()
    private var updateData: HashMap<String, AttendanceDetail> = HashMap()

    private val selectedItems = SparseBooleanArray()
    private val animationItemsIndex = SparseBooleanArray()
    private var reverseAllAnimations = false
    private var currentSelectedIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_time_clock_attendance, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) lumperAttendanceFilteredList.size else lumperAttendanceList.size
    }

    private fun getItem(position: Int): LumperAttendanceData {
        return if (searchEnabled) lumperAttendanceFilteredList[position] else lumperAttendanceList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener, TextWatcher, View.OnLongClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val viewAttendanceStatus: View = view.viewAttendanceStatus
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val checkBoxAttendance: CheckBox = view.checkBoxAttendance
        private val textViewEmployeeId: CustomTextView = view.textViewEmployeeId
        private val textViewAddTime: TextView = view.textViewAddTime
        private val textViewNoTimeLoggedIn: TextView = view.textViewNoTimeLoggedIn
        private val linearLayoutLumperTime: LinearLayout = view.linearLayoutLumperTime
        private val textViewShiftTime: TextView = view.textViewShiftTime
        private val textViewLunchTime: TextView = view.textViewLunchTime
        private val editTextNotes: EditText = view.editTextNotes
        private val relativeLayoutSelected: RelativeLayout = view.relativeLayoutSelected
        private val layoutCheckBox: RelativeLayout = view.layoutCheckBox
        private val constraintLayout: ConstraintLayout = view.constraintLayout

        init {
            itemView.setOnLongClickListener(this)
            itemView.setOnClickListener(this)
            layoutCheckBox.setOnClickListener(this)
        }

        fun bind(lumperAttendance: LumperAttendanceData) {
            // handle icon animation
            applyIconAnimation(adapterPosition)

            UIUtils.showEmployeeProfileImage(context, lumperAttendance, circleImageViewProfile)
            textViewLumperName.text = UIUtils.getEmployeeFullName(lumperAttendance)
            textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(lumperAttendance)

            lumperAttendance.attendanceDetail?.let { attendanceDetail ->
                val isPresent = getDefaultOrValue(attendanceDetail.isPresent)
                if (isPresent) {
                    val morningPunchIn = convertDateStringToTime(PATTERN_API_RESPONSE, attendanceDetail.morningPunchIn)
                    val eveningPunchOut = convertDateStringToTime(PATTERN_API_RESPONSE, attendanceDetail.eveningPunchOut)
                    textViewShiftTime.text = String.format(
                        "%s - %s",
                        if (morningPunchIn.isNotEmpty()) morningPunchIn else "NA",
                        if (eveningPunchOut.isNotEmpty()) eveningPunchOut else "NA"
                    )

                    val lunchPunchIn = convertDateStringToTime(PATTERN_API_RESPONSE, attendanceDetail.lunchPunchIn)
                    val lunchPunchOut = convertDateStringToTime(PATTERN_API_RESPONSE, attendanceDetail.lunchPunchOut)
                    textViewLunchTime.text = String.format(
                        "%s - %s",
                        if (lunchPunchIn.isNotEmpty()) lunchPunchIn else "NA",
                        if (lunchPunchOut.isNotEmpty()) lunchPunchOut else "NA"
                    )
                }
                editTextNotes.setText(attendanceDetail.attendanceNote)
                updateTimeUI(isPresent, lumperAttendance.id!!)
            }

            textViewAddTime.setOnClickListener(this)

            val isSelected = selectedItems.get(adapterPosition, false)
            constraintLayout.isActivated = isSelected

            textViewAddTime.visibility = if (textViewAddTime.visibility == View.VISIBLE && selectedItems.size() == 0) View.VISIBLE else View.GONE
            editTextNotes.isEnabled = selectedItems.size() == 0
            layoutCheckBox.visibility = if (isSelected) View.VISIBLE else View.GONE
        }

        private fun updateTimeUI(isPresent: Boolean, lumperId: String) {
            viewAttendanceStatus.setBackgroundResource(if (isPresent) R.drawable.online_dot else R.drawable.offline_dot)
            checkBoxAttendance.isChecked = isPresent
            checkBoxAttendance.isEnabled = checkIfEditable(isPresent, ATTENDANCE_IS_PRESENT, lumperId)
            textViewAddTime.visibility = if (isPresent) View.VISIBLE else View.GONE
            linearLayoutLumperTime.visibility = if (isPresent) View.VISIBLE else View.GONE
            textViewNoTimeLoggedIn.visibility = if (isPresent) View.GONE else View.VISIBLE

            checkBoxAttendance.setOnClickListener(this)
            editTextNotes.addTextChangedListener(this)
        }

        private fun applyIconAnimation(position: Int) {
            if (selectedItems[position, false]) {
                circleImageViewProfile.visibility = View.GONE
                resetIconYAxis(relativeLayoutSelected)
                relativeLayoutSelected.visibility = View.VISIBLE
                relativeLayoutSelected.alpha = 1f
                if (currentSelectedIndex == position) {
                    FlipAnimator.flipView(context, relativeLayoutSelected, circleImageViewProfile, true)
                    resetCurrentIndex()
                }
            } else {
                relativeLayoutSelected.visibility = View.GONE
                resetIconYAxis(circleImageViewProfile)
                circleImageViewProfile.visibility = View.VISIBLE
                circleImageViewProfile.alpha = 1f
                if (reverseAllAnimations && animationItemsIndex[position, false] || currentSelectedIndex == position) {
                    FlipAnimator.flipView(context, relativeLayoutSelected, circleImageViewProfile, false)
                    resetCurrentIndex()
                }
            }
        }

        override fun onLongClick(view: View?): Boolean {
            view?.let {
                return when (view.id) {
                    itemView.id -> {
                        val isPresent = getItem(adapterPosition).attendanceDetail?.isPresent
                        isPresent?.let {
                            if (isPresent) {
                                onAdapterClick.onRowLongClicked(adapterPosition)
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                return true
                            }
                        }
                        Toast.makeText(context, "Lumper isn't arrived yet", Toast.LENGTH_SHORT).show()
                        false
                    }
                    else -> {
                        false
                    }
                }
            }
            return false
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        if (getSelectedItemCount() > 0) {
                            val isPresent = getItem(adapterPosition).attendanceDetail?.isPresent
                            isPresent?.let {
                                if (isPresent) {
                                    onAdapterClick.onRowClicked(adapterPosition)
                                } else {
                                    Toast.makeText(context, "Lumper isn't arrived yet", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    textViewAddTime.id -> {
                        onAdapterClick.onAddTimeClick(getItem(adapterPosition), adapterPosition)
                    }
                    checkBoxAttendance.id -> {
                        val isChecked = checkBoxAttendance.isChecked

                        val item = getItem(adapterPosition)
                        // Update in API Request Object
                        changeIsPresentRecord(item.id, isPresent = isChecked)

                        //Update in Local List Object to show changes on UI
                        getItem(adapterPosition).attendanceDetail?.isPresent = isChecked

                        notifyDataSetChanged()
                    }
                }
            }
        }

        override fun afterTextChanged(text: Editable?) {
            val item = getItem(adapterPosition)
            if (getDefaultOrValue(item.attendanceDetail?.attendanceNote) != text.toString()) {
                // Update in API Request Object
                changeNotesRecord(item.id)
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

    fun updateList(lumperAttendanceList: ArrayList<LumperAttendanceData>, currentPageIndex: Int) {
        if (currentPageIndex == 1) {
            this.updateData.clear()
            this.lumperAttendanceList.clear()
        }

        this.lumperAttendanceList.addAll(lumperAttendanceList)
        notifyDataSetChanged()
    }

    private fun changeIsPresentRecord(lumperId: String?, isPresent: Boolean) {
        if (!lumperId.isNullOrEmpty() && !updateData.containsKey(lumperId)) {
            updateData[lumperId] = AttendanceDetail()
            updateData[lumperId]?.lumperId = lumperId
        }
        updateData[lumperId]?.isPresent = isPresent
        updateData[lumperId]?.isPresentChanged = true
    }

    private fun changeNotesRecord(lumperId: String?) {
        if (!lumperId.isNullOrEmpty() && !updateData.containsKey(lumperId)) {
            updateData[lumperId] = AttendanceDetail()
            updateData[lumperId]?.lumperId = lumperId
        }
    }

    private fun initiateUpdateRecord(lumperId: String?, isPresent: Boolean = true) {
        if (!lumperId.isNullOrEmpty() && !updateData.containsKey(lumperId)) {
            updateData[lumperId] = AttendanceDetail()
            updateData[lumperId]?.lumperId = lumperId
            updateData[lumperId]?.isPresent = isPresent
        }
    }

    fun updateClockInTime(itemPosition: Int, currentTime: Long, isNotify: Boolean = true) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.morningPunchIn = "${currentTime / 1000L}"
        updateData[item.id]?.isMorningPunchInChanged = true

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.morningPunchIn = DateUtils.getUTCDateString(PATTERN_API_RESPONSE, Date(currentTime))

        if (isNotify)
            notifyDataSetChanged()
    }

    fun updateClockOutTime(itemPosition: Int, currentTime: Long, isNotify: Boolean = true) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.eveningPunchOut = "${currentTime / 1000L}"
        updateData[item.id]?.isEveningPunchOutChanged = true

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.eveningPunchOut = DateUtils.getUTCDateString(PATTERN_API_RESPONSE, Date(currentTime))

        if (isNotify)
            notifyDataSetChanged()
    }

    fun updateLunchInTime(itemPosition: Int, currentTime: Long, isNotify: Boolean = true) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.lunchPunchIn = "${currentTime / 1000L}"
        updateData[item.id]?.isLunchPunchInChanged = true

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.lunchPunchIn = DateUtils.getUTCDateString(PATTERN_API_RESPONSE, Date(currentTime))

        if (isNotify)
            notifyDataSetChanged()
    }

    fun updateLunchOutTime(itemPosition: Int, currentTime: Long, isNotify: Boolean = true) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.lunchPunchOut = "${currentTime / 1000L}"
        updateData[item.id]?.isLunchPunchOutChanged = true

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.lunchPunchOut = DateUtils.getUTCDateString(PATTERN_API_RESPONSE, Date(currentTime))

        if (isNotify)
            notifyDataSetChanged()
    }

    fun getUpdatedData(): HashMap<String, AttendanceDetail> {
        return updateData
    }

    fun checkIfEditable(hasValue: Boolean, variableName: String, lumperId: String): Boolean {
        return if (updateData.containsKey(lumperId)) {
            val isChanged = when (variableName) {
                ATTENDANCE_IS_PRESENT -> updateData[lumperId]?.isPresentChanged!!
                ATTENDANCE_MORNING_PUNCH_IN -> updateData[lumperId]?.isMorningPunchInChanged!!
                ATTENDANCE_EVENING_PUNCH_OUT -> updateData[lumperId]?.isEveningPunchOutChanged!!
                ATTENDANCE_LUNCH_PUNCH_IN -> updateData[lumperId]?.isLunchPunchInChanged!!
                ATTENDANCE_LUNCH_PUNCH_OUT -> updateData[lumperId]?.isLunchPunchOutChanged!!
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
    }

    fun updateClockInTimeForSelectedPositions(currentTime: Long) {
        val positions = getSelectedItemPositions()
        for (position in positions) {
            updateClockInTime(position, currentTime, isNotify = false)
        }

        selectedItems.clear()
        resetCurrentIndex()
        notifyDataSetChanged()
    }

    fun updateClockOutTimeForSelectedPositions(currentTime: Long) {
        val positions = getSelectedItemPositions()
        for (position in positions) {
            updateClockOutTime(position, currentTime, isNotify = false)
        }

        selectedItems.clear()
        resetCurrentIndex()
        notifyDataSetChanged()
    }

    fun updateLunchInTimeForSelectedPositions(currentTime: Long) {
        val positions = getSelectedItemPositions()
        for (position in positions) {
            updateLunchInTime(position, currentTime, isNotify = false)
        }

        selectedItems.clear()
        resetCurrentIndex()
        notifyDataSetChanged()
    }

    fun updateLunchOutTimeForSelectedPositions(currentTime: Long) {
        val positions = getSelectedItemPositions()
        for (position in positions) {
            updateLunchOutTime(position, currentTime, isNotify = false)
        }

        selectedItems.clear()
        resetCurrentIndex()
        notifyDataSetChanged()
    }

    //////// MultiSelection Functions///////////////
    fun toggleSelection(position: Int) {
        currentSelectedIndex = position
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position)
            animationItemsIndex.delete(position)
        } else {
            selectedItems.put(position, true)
            animationItemsIndex.put(position, true)
        }
        notifyDataSetChanged()
    }

    fun getSelectedItemCount(): Int {
        return selectedItems.size()
    }

    private fun getSelectedItemPositions(): ArrayList<Int> {
        val items = ArrayList<Int>(selectedItems.size())
        for (position in selectedItems.keyIterator()) {
            items.add(position)
        }
        return items
    }

    fun getSelectedItems(): ArrayList<LumperAttendanceData> {
        val items = ArrayList<LumperAttendanceData>(selectedItems.size())
        for (position in selectedItems.keyIterator()) {
            items.add(getItem(position))
        }
        return items;
    }

    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    fun resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }
    ////////////////////////////////////////////
}
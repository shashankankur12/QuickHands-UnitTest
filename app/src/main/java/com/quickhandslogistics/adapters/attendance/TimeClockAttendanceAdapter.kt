package com.quickhandslogistics.adapters.attendance

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.util.keyIterator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.flexbox.FlexboxLayout
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.attendance.TimeClockAttendanceContract
import com.quickhandslogistics.controls.CustomTextView
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_EVENING_PUNCH_OUT
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_IS_PRESENT
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_LUNCH_PUNCH_IN
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_LUNCH_PUNCH_OUT
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_MORNING_PUNCH_IN
import com.quickhandslogistics.utils.ConnectionDetector
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_RESPONSE
import com.quickhandslogistics.utils.DateUtils.Companion.convertDateStringToTime
import com.quickhandslogistics.utils.DateUtils.Companion.getDateTimeCalculeted
import com.quickhandslogistics.utils.FlipAnimatorUtil
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
        private val textViewCheckBoxStatus: CustomTextView = view.textViewCheckBoxStatus
        private val textViewEmployeeId: CustomTextView = view.textViewEmployeeId
        private val textViewAddTime: TextView = view.textViewAddTime
        private val textViewNoTimeLoggedIn: TextView = view.textViewNoTimeLoggedIn
        private val flexboxLayoutLumperTime: FlexboxLayout = view.flexboxLayoutLumperTime
        private val textViewShiftTime: TextView = view.textViewShiftTime
        private val textViewLunchTime: TextView = view.textViewLunchTime
        private val textViewShiftTotalTime: TextView = view.textViewShiftTotalTime
        private val textViewLunchTotalTime: TextView = view.textViewLunchTotalTime
        private val editTextNotes: EditText = view.editTextNotes
        private val relativeLayoutSelected: RelativeLayout = view.relativeLayoutSelected
        private val layoutCheckBox: RelativeLayout = view.layoutCheckBox
        private val layoutEditTimeClock: RelativeLayout = view.layoutEditTimeClock
        private val constraintLayout: ConstraintLayout = view.constraintLayout

        init {
            itemView.setOnLongClickListener(this)
            itemView.setOnClickListener(this)
        }

        fun bind(lumperAttendance: LumperAttendanceData) {
            // handle icon animation
            applyIconAnimation(adapterPosition)

            UIUtils.showEmployeeProfileImage(context, lumperAttendance, circleImageViewProfile)
            UIUtils.updateProfileBorder(context, lumperAttendance.isTemporaryAssigned, circleImageViewProfile)
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

                    if(!attendanceDetail.morningPunchIn.isNullOrEmpty()&& !attendanceDetail.eveningPunchOut.isNullOrEmpty())
                        textViewShiftTotalTime.text=String.format("Total Time: %s",getDateTimeCalculeted(attendanceDetail.morningPunchIn!!, attendanceDetail.eveningPunchOut!!))
                    else textViewShiftTotalTime.text=""

                    val lunchPunchIn = convertDateStringToTime(PATTERN_API_RESPONSE, attendanceDetail.lunchPunchIn)
                    val lunchPunchOut = convertDateStringToTime(PATTERN_API_RESPONSE, attendanceDetail.lunchPunchOut)
                    textViewLunchTime.text = String.format(
                        "%s - %s",
                        if (lunchPunchIn.isNotEmpty()) lunchPunchIn else "NA",
                        if (lunchPunchOut.isNotEmpty()) lunchPunchOut else "NA"
                    )
                    if(!attendanceDetail.lunchPunchIn.isNullOrEmpty()&&!attendanceDetail.lunchPunchOut.isNullOrEmpty())
                        textViewLunchTotalTime.text=String.format("Total Time: %s",getDateTimeCalculeted(attendanceDetail.lunchPunchIn!!, attendanceDetail.lunchPunchOut!!))
                    else textViewLunchTotalTime.text=""
                    textViewAddTime.visibility = /*if( attendanceDetail?.eveningPunchOut == null) View.VISIBLE else*/ View.GONE
                }else{
                    textViewShiftTime.text = String.format("%s - %s", "NA", "NA")
                    textViewLunchTime.text = String.format("%s - %s", "NA", "NA")
                    textViewShiftTotalTime.text=""
                    textViewLunchTotalTime.text=""

                }
                editTextNotes.setText(attendanceDetail.attendanceNote)
                updateTimeUI(isPresent, lumperAttendance.id!!)
                ishaseClockOut(lumperAttendance)
            }



            textViewAddTime.setOnClickListener(this)
            flexboxLayoutLumperTime.setOnClickListener(this)
            layoutCheckBox.setOnClickListener(this)

            val isSelected = selectedItems.get(adapterPosition, false)
            constraintLayout.isActivated = isSelected
            checkBoxAttendance.isChecked = isSelected
            flexboxLayoutLumperTime.isClickable = selectedItems.size() <= 0

            textViewAddTime.visibility = if (textViewAddTime.visibility == View.VISIBLE && selectedItems.size() == 0&& lumperAttendance?.attendanceDetail?.eveningPunchOut == null) View.VISIBLE else View.GONE
            editTextNotes.isEnabled = selectedItems.size() == 0
            layoutCheckBox.visibility = /*if (isSelected) View.VISIBLE else*/ View.GONE
        }

        private fun ishaseClockOut(lumperAttendance: LumperAttendanceData) {
            lumperAttendance.attendanceDetail?.let {
                if ( !it.morningPunchIn.isNullOrEmpty() && it.eveningPunchOut.isNullOrEmpty()){
                    viewAttendanceStatus.setBackgroundResource( R.drawable.online_dot )
                }else if(!it.morningPunchIn.isNullOrEmpty() && !it.eveningPunchOut.isNullOrEmpty()){
                    viewAttendanceStatus.setBackgroundResource( R.drawable.offline_dot)

                }else viewAttendanceStatus.setBackgroundResource( R.drawable.offline_dot)
            }
        }

        private fun updateTimeUI(isPresent: Boolean, lumperId: String) {
//            checkBoxAttendance.isChecked = false
//            checkBoxAttendance.isEnabled = checkIfEditable(isPresent, ATTENDANCE_IS_PRESENT, lumperId)
            textViewAddTime.visibility = /*if (isPresent) View.VISIBLE else*/ View.GONE
            flexboxLayoutLumperTime.visibility = /*if (isPresent)*/ View.VISIBLE /*else View.GONE*/
            textViewNoTimeLoggedIn.visibility = if (isPresent) View.GONE else View.VISIBLE

            checkBoxAttendance.setOnClickListener(this)
            editTextNotes.addTextChangedListener(this)

            if (!checkBoxAttendance.isChecked) {
                textViewCheckBoxStatus.text = context.getString(R.string.mark_present)
            } else if (checkBoxAttendance.isEnabled) {
                textViewCheckBoxStatus.text = context.getString(R.string.change_present)
            } else {
                textViewCheckBoxStatus.text = context.getString(R.string.present)
            }
            textViewCheckBoxStatus.visibility=View.GONE
        }

        private fun applyIconAnimation(position: Int) {
            if (selectedItems[position, false]) {
                circleImageViewProfile.visibility = View.GONE
                resetIconYAxis(relativeLayoutSelected)
                relativeLayoutSelected.visibility = View.VISIBLE
                relativeLayoutSelected.alpha = 1f
                if (currentSelectedIndex == position) {
                    FlipAnimatorUtil.flipView(context, relativeLayoutSelected, circleImageViewProfile, true)
                    resetCurrentIndex()
                }
            } else {
                relativeLayoutSelected.visibility = View.GONE
                resetIconYAxis(circleImageViewProfile)
                circleImageViewProfile.visibility = View.VISIBLE
                circleImageViewProfile.alpha = 1f
                if (reverseAllAnimations && animationItemsIndex[position, false] || currentSelectedIndex == position) {
                    FlipAnimatorUtil.flipView(context, relativeLayoutSelected, circleImageViewProfile, false)
                    resetCurrentIndex()
                }
            }
        }

        override fun onLongClick(view: View?): Boolean {

            view?.let {
                return when (view.id) {
//                    itemView.id -> {
//                        val isPresent = getItem(adapterPosition).attendanceDetail?.isPresent
//                        isPresent?.let {
//                            if (isPresent) {
//                                onAdapterClick.onRowLongClicked(adapterPosition)
//                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
//                                return true
//                            }
//                        }
//                        Toast.makeText(context, "Lumper isn't arrived yet", Toast.LENGTH_SHORT).show()
//                        false
//                    }
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
                    /*itemView.id -> {
                        if (getSelectedItemCount() > 0) {
                            val isPresent = getItem(adapterPosition).attendanceDetail?.isPresent
//                            isPresent?.let {
                                if (checkBoxAttendance.isChecked) {
                                    onAdapterClick.onRowClicked(adapterPosition)
                                } else {
                                    Toast.makeText(context, "Lumper isn't arrived yet", Toast.LENGTH_SHORT).show()
                                }
//                            }
                        }
                    }*/
                    flexboxLayoutLumperTime.id -> {
                        if (!ConnectionDetector.isNetworkConnected(context)) {
                            ConnectionDetector.createSnackBar(context as Activity?)
                            return
                        }

                        onAdapterClick.onAddTimeClick(getItem(adapterPosition), adapterPosition)
                    }
                    checkBoxAttendance.id -> {
                        if (!ConnectionDetector.isNetworkConnected(context)) {
                            ConnectionDetector.createSnackBar(context as Activity?)
                            return
                        }
//
//                        val item = getItem(adapterPosition)
//                        // Update in API Request Object
//                        changeIsPresentRecord(item.id, isPresent = isChecked)
//
//                        //Update in Local List Object to show changes on UId
//                        getItem(adapterPosition).attendanceDetail?.isPresent = isChecked
                        if (getSelectedItemCount() > 0){
                            onAdapterClick.onRowClicked(adapterPosition)
                        }else{
                            onAdapterClick.onRowLongClicked(adapterPosition)
                        }

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

    fun isSearchEnabled(): Boolean {
        return searchEnabled
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
        this.lumperAttendanceList.addAll(lumperAttendanceList)
        notifyDataSetChanged()
    }

    fun updatePresentRecord(adapterPosition: Int, isChecked :Boolean) {

        val item = getItem(adapterPosition)
        // Update in API Request Object
        changeIsPresentRecord(item.id, isPresent = isChecked)

        //Update in Local List Object to show changes on UI
        getItem(adapterPosition).attendanceDetail?.isPresent = isChecked

    }

    private fun changeIsPresentRecord(lumperId: String?, isPresent: Boolean) {
        if (!lumperId.isNullOrEmpty() && !updateData.containsKey(lumperId)) {
            updateData[lumperId] = AttendanceDetail()
            updateData[lumperId]?.lumperId = lumperId
        }
        updateData[lumperId]?.isPresent = isPresent
        updateData[lumperId]?.isPresentChanged = true
    }

    fun clearPresentRecord(adapterPosition: Int, isChecked :Boolean) {

        val item = getItem(adapterPosition)
        // Update in API Request Object
        clearIsPresentRecord(item.id, isPresent = isChecked)

        //Update in Local List Object to show changes on UI
        getItem(adapterPosition).attendanceDetail?.isPresent = isChecked

    }


    private fun clearIsPresentRecord(lumperId: String?, isPresent: Boolean) {
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

    private fun reamoveUpdateRecord(lumperId: String?, isPresent: Boolean = true) {
        if (!lumperId.isNullOrEmpty() && updateData.containsKey(lumperId)) {
            if ( !updateData[lumperId]?.lunchPunchIn.isNullOrEmpty()!! || !updateData[lumperId]?.eveningPunchOut.isNullOrEmpty()) {
                updateData[lumperId]?.lumperId = lumperId
                updateData[lumperId]?.isPresent = isPresent
            } else if (updateData.containsKey(lumperId)){
                    updateData.remove(lumperId)
                }

        }
    }

    fun updateClockInTime(itemPosition: Int, currentTime: Long, isNotify: Boolean = true) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        initiateUpdateRecord(item.id)
        updateData[item.id]?.morningPunchIn = "$currentTime"
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
        updateData[item.id]?.eveningPunchOut = "$currentTime"
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
        updateData[item.id]?.lunchPunchIn = "$currentTime"
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
        updateData[item.id]?.lunchPunchOut = "$currentTime"
        updateData[item.id]?.isLunchPunchOutChanged = true

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.lunchPunchOut = DateUtils.getUTCDateString(PATTERN_API_RESPONSE, Date(currentTime))

        if (isNotify)
            notifyDataSetChanged()
    }

    fun clearClockInTime(itemPosition: Int, isNotify: Boolean = true) {
        val item = getItem(itemPosition)

        // Update in API Request Object
       if( updateData.containsKey(item.id)){
           updateData.remove(item.id)
       }

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.morningPunchIn =null

        if (isNotify)
            notifyDataSetChanged()
    }

    fun clearClockOutTime(itemPosition: Int, isNotify: Boolean = true) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        if (updateData.containsKey(item.id)) {
            updateData[item.id]?.eveningPunchOut = null
            updateData[item.id]?.isEveningPunchOutChanged = false
        }
        reamoveUpdateRecord(item.id)
//        if( updateData.containsKey(item.id)){
//            updateData.remove(item.id)
//        }

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.eveningPunchOut = null
        if (isNotify)
            notifyDataSetChanged()
    }

    fun clearLunchInTime(itemPosition: Int,isNotify: Boolean = true) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        if (updateData.containsKey(item.id)){
            updateData[item.id]?.lunchPunchIn = null
            updateData[item.id]?.isLunchPunchInChanged = false
        }

        reamoveUpdateRecord(item.id)
//        if( updateData.containsKey(item.id)){
//            updateData.remove(item.id)
//        }

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.lunchPunchIn = null

        if (isNotify)
            notifyDataSetChanged()
    }

    fun clearLunchOutTime(itemPosition: Int, isNotify: Boolean = true) {
        val item = getItem(itemPosition)

        // Update in API Request Object
        if( updateData.containsKey(item.id)){
            updateData.remove(item.id)
        }

        //Update in Local List Object to show changes on UI
        getItem(itemPosition).attendanceDetail?.lunchPunchOut = null

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
            updatePresentRecord(position, true)
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
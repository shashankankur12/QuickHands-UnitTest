package com.quickhandslogistics.adapters.workSheet

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailLumpersContract
import com.quickhandslogistics.controls.CustomTextView
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.data.workSheet.PauseTime
import com.quickhandslogistics.data.workSheet.PauseTimeRequest
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.convertDateStringToTime
import com.quickhandslogistics.utils.ScheduleUtils.calculatePercent
import com.quickhandslogistics.utils.UIUtils
import com.quickhandslogistics.utils.ValueUtils
import com.quickhandslogistics.utils.ValueUtils.getDefaultOrValue
import com.quickhandslogistics.utils.ValueUtils.isNumeric
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_work_item_detail_lumper_time.view.*

class WorkSheetItemDetailLumpersAdapter(private val resources: Resources, private var onAdapterClick: WorkSheetItemDetailLumpersContract.View.OnAdapterItemClickListener) :
    Adapter<WorkSheetItemDetailLumpersAdapter.ViewHolder>() {

    private var workItemStatus = ""
    private var totalCases = ""
    private var isCompleted: Boolean= false
    private var tempLumperIds = ArrayList<String>()
    private var lumperList = ArrayList<LumperAttendanceData>()
    private var timingsData = HashMap<String, LumpersTimeSchedule>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_work_item_detail_lumper_time, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return lumperList.size
    }

    private fun getItem(position: Int): LumperAttendanceData {
        return lumperList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val linearLayoutLumperTime: LinearLayout = view.linearLayoutLumperTime as LinearLayout
        private val textViewEmployeeId: CustomTextView = view.textViewEmployeeId
        private val textViewAddTime: TextView = view.textViewAddTime
        private val textViewWorkTime: TextView = view.textViewWorkTime
        private val textViewWaitingTime: TextView = view.textViewWaitingTime
        private val textViewBreakTime: TextView = view.textViewBreakTime
        private val textViewWorkDone: TextView = view.textViewWorkDone
        private val viewAttendanceStatus: View = view.viewAttendanceStatus
        private val imageViewCancelLumper: ImageView = view.imageViewCancelLumper

        fun bind(employeeData: LumperAttendanceData) {
            UIUtils.showEmployeeProfileImage(
                context,
                employeeData.profileImageUrl,
                circleImageViewProfile
            )
            UIUtils.updateProfileBorder(
                context,
                tempLumperIds.contains(employeeData.id),
                circleImageViewProfile
            )
            textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)
            textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(employeeData)
            ishaseClockOut(employeeData)

            if (timingsData.containsKey(employeeData.id)) {
                val timingDetail = timingsData[employeeData.id]
                timingDetail?.let {
                    val startTime = convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.startTime)
                    val endTime = convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.endTime)
                    if (startTime.isNotEmpty() && endTime.isNotEmpty())
                        textViewWorkTime.text = String.format("%s - %s : %s", startTime, endTime, DateUtils.getDateTimeCalculeted(timingDetail.startTime!!, timingDetail.endTime!!))
                    else textViewWorkTime.text = String.format("%s - %s", if (startTime.isNotEmpty()) startTime else AppConstant.NOTES_NOT_AVAILABLE,
                        if (endTime.isNotEmpty()) endTime else AppConstant.NOTES_NOT_AVAILABLE
                    )

                    val waitingTime = getDefaultOrValue(timingDetail.waitingTime)
                    if (waitingTime.isNotEmpty() && waitingTime.toInt() != 0) {
                        val waitingTimeHours = ValueUtils.getHoursFromMinutes(timingDetail.waitingTime)
                        val waitingTimeMinutes = ValueUtils.getRemainingMinutes(timingDetail.waitingTime)
                        textViewWaitingTime.text = String.format("%s H %s M", waitingTimeHours, waitingTimeMinutes )
                    } else {
                        textViewWaitingTime.text = AppConstant.NOTES_NOT_AVAILABLE
                    }

                    var mBreakTimeList = getBreakTimeList(timingDetail.breakTimes)
                    if (mBreakTimeList.isNotEmpty()) {
                        showPauseTimeDuration(mBreakTimeList, textViewBreakTime)
                    } else textViewBreakTime.text = AppConstant.NOTES_NOT_AVAILABLE

                    if (!timingDetail.partWorkDone.isNullOrEmpty() && timingDetail.partWorkDone!!.toInt() != 0) {
                        if (!totalCases.isNullOrEmpty() && isNumeric(totalCases)) {
                            val parcetage = String.format("%.2f", calculatePercent(timingDetail.partWorkDone!!, totalCases)) + "%"
                            textViewWorkDone.text = String.format("%s / %s : %s", timingDetail.partWorkDone, totalCases, parcetage) }
                    } else {
                        textViewWorkDone.text = AppConstant.NOTES_NOT_AVAILABLE
                    }
                }
            } else {
                textViewWorkTime.text = "${AppConstant.NOTES_NOT_AVAILABLE} - ${AppConstant.NOTES_NOT_AVAILABLE}"
                textViewWaitingTime.text = AppConstant.NOTES_NOT_AVAILABLE
                textViewWorkDone.text = AppConstant.NOTES_NOT_AVAILABLE
                textViewBreakTime.text = "${AppConstant.NOTES_NOT_AVAILABLE} - ${AppConstant.NOTES_NOT_AVAILABLE}"
            }

            changeAddButtonVisibility()
            textViewAddTime.setOnClickListener(this)
            linearLayoutLumperTime.setOnClickListener(this)
            imageViewCancelLumper.setOnClickListener(this)
        }

        private fun ishaseClockOut(lumperAttendance: LumperAttendanceData) {
            lumperAttendance.attendanceDetail?.let {
                if (it.isPresent!! && !it.morningPunchIn.isNullOrEmpty() && it.eveningPunchOut.isNullOrEmpty()){
                    viewAttendanceStatus.setBackgroundResource( R.drawable.online_dot )
                }else if(it.isPresent!! && !it.morningPunchIn.isNullOrEmpty() && !it.eveningPunchOut.isNullOrEmpty()){
                    viewAttendanceStatus.setBackgroundResource( R.drawable.offline_dot)

                }else viewAttendanceStatus.setBackgroundResource( R.drawable.offline_dot)
            }
        }

        private fun getBreakTimeList(breakTimes: ArrayList<PauseTime>?): ArrayList<PauseTimeRequest> {
            val pauseTimeList: ArrayList<PauseTimeRequest> = ArrayList()
            breakTimes?.let {
                it.forEach {
                    val breakTime = PauseTimeRequest()
                    breakTime.startTime = convertInitialTime(it.startTime)
                    breakTime.endTime = convertInitialTime(it.endTime)
                    pauseTimeList.add(breakTime)
                }
            }
            return pauseTimeList
        }

        private fun convertInitialTime(dateStamp: String?): Long {
            var milliseconds: Long = 0
            val time = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, dateStamp)
            if (time.isNotEmpty()) {
                val currentDateString = DateUtils.convertUTCDateStringToLocalDateString(
                    DateUtils.PATTERN_API_RESPONSE,
                    dateStamp
                )
                milliseconds = DateUtils.getMillisecondsFromDateString(
                    DateUtils.PATTERN_API_RESPONSE,
                    currentDateString
                )
            }
            return milliseconds
        }

        private fun showPauseTimeDuration(
            mBreakTimeList: ArrayList<PauseTimeRequest>,
            textViewBreakTime: TextView
        ) {
            var dateTime: Long = 0
            for (pauseTime in mBreakTimeList) {
                dateTime += (pauseTime.endTime!! - pauseTime.startTime!!)
            }
            textViewBreakTime.text = "${DateUtils.getDateTimeCalculatedLong(dateTime)}"
        }

        private fun changeAddButtonVisibility() {
            if (workItemStatus == AppConstant.WORK_ITEM_STATUS_IN_PROGRESS || workItemStatus == AppConstant.WORK_ITEM_STATUS_ON_HOLD ||workItemStatus == AppConstant.WORK_ITEM_STATUS_SCHEDULED||workItemStatus == AppConstant.WORK_ITEM_STATUS_CANCELLED ||workItemStatus == AppConstant.WORK_ITEM_STATUS_COMPLETED) {
//                textViewAddTime.visibility = if (isCompleted) View.GONE else View.VISIBLE
                textViewAddTime.visibility = View.GONE
                linearLayoutLumperTime.isClickable = !isCompleted
            } else {
                textViewAddTime.visibility = View.GONE
                linearLayoutLumperTime.isClickable = false
            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    linearLayoutLumperTime.id -> {
                        val employeeData = getItem(adapterPosition)
                        val timingData = timingsData[employeeData.id]
                        onAdapterClick.onAddTimeClick(employeeData, timingData)
                    }
                    imageViewCancelLumper.id->{
                        val employeeData = getItem(adapterPosition)
                        onAdapterClick.onRemoveLumperClick(employeeData, adapterPosition)
                    }
                }
            }
        }
    }

    fun updateList(lumperList: ArrayList<LumperAttendanceData>?, timingsData: LinkedHashMap<String, LumpersTimeSchedule>, status: String? = "", tempLumperIds: ArrayList<String>, totalCases: String?, isCompleted: Boolean?) {
        this.timingsData.clear()
        this.lumperList.clear()
        lumperList?.let {
            this.lumperList.addAll(lumperList)
            this.timingsData.putAll(timingsData)
        }
        this.workItemStatus = getDefaultOrValue(status)
        this.totalCases=getDefaultOrValue(totalCases)
        this.isCompleted= getDefaultOrValue(isCompleted)

        this.tempLumperIds.clear()
        this.tempLumperIds.addAll(tempLumperIds)
        notifyDataSetChanged()
    }
}
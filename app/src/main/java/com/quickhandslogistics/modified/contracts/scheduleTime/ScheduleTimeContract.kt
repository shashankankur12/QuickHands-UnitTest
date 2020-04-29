package com.quickhandslogistics.modified.contracts.scheduleTime

import com.quickhandslogistics.modified.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeNotes
import java.util.*

class ScheduleTimeContract {
    interface Model {
        fun fetchSchedulesTimeByDate(
            selectedDate: Date,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(selectedDate: Date, scheduleTimeAPIResponse: GetScheduleTimeAPIResponse)
        }
    }

    interface View {
        fun showDateString(dateString: String)
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun showScheduleTimeData(
            selectedDate: Date, scheduleTimeDetailList: ArrayList<ScheduleTimeDetail>
        )
        fun showNotesData(notes: ScheduleTimeNotes?)
    }

    interface Presenter {
        fun getSchedulesTimeByDate(date: Date)
        fun onDestroy()
    }
}
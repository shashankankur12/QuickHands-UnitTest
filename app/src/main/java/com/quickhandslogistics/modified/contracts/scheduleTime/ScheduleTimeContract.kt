package com.quickhandslogistics.modified.contracts.scheduleTime

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeNotes
import java.util.*

class ScheduleTimeContract {
    interface Model {
        fun fetchSchedulesTimeByDate(selectedDate: Date, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(selectedDate: Date, scheduleTimeAPIResponse: GetScheduleTimeAPIResponse)
        }
    }

    interface View : BaseContract.View {
        fun showDateString(dateString: String)
        fun showAPIErrorMessage(message: String)
        fun showNotesData(notes: ScheduleTimeNotes?)
        fun showScheduleTimeData(selectedDate: Date, scheduleTimeDetailList: ArrayList<ScheduleTimeDetail>)
    }

    interface Presenter : BaseContract.Presenter {
        fun getSchedulesTimeByDate(date: Date)
    }
}
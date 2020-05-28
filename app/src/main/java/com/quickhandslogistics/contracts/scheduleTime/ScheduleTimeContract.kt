package com.quickhandslogistics.contracts.scheduleTime

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import java.util.*
import kotlin.collections.ArrayList

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
        fun showNotesData(notes: String?)
        fun showScheduleTimeData(selectedDate: Date, scheduleTimeDetailList: ArrayList<ScheduleTimeDetail>, tempLumperIds: ArrayList<String>)
    }

    interface Presenter : BaseContract.Presenter {
        fun getSchedulesTimeByDate(date: Date)
    }
}
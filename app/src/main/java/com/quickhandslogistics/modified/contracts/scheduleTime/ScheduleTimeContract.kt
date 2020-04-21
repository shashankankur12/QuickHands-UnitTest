package com.quickhandslogistics.modified.contracts.scheduleTime

import com.quickhandslogistics.modified.data.schedule.ScheduleListAPIResponse
import java.util.*

class ScheduleTimeContract {
    interface Model {
        fun fetchSchedulesTimeByDate(
            selectedDate: Date,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(
                selectedDate: Date,
                scheduleListAPIResponse: ScheduleListAPIResponse
            )
        }
    }

    interface View {
        fun showDateString(dateString: String)
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun showScheduleTimeData(selectedDate: Date)

        interface OnAdapterItemClickListener {
//            fun onScheduleItemClick(scheduleDetail: ScheduleDetail)
        }
    }

    interface Presenter {
        fun getSchedulesTimeByDate(date: Date)
        fun onDestroy()
    }
}
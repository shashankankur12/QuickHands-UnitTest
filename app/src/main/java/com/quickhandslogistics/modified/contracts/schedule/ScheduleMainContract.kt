package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.schedule.ScheduleMainResponse
import java.util.*

class ScheduleMainContract {
    interface Model {
        fun fetchSchedules(
            selectedDate: Date,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(
                selectedDate: Date,
                scheduleMainResponse: ScheduleMainResponse
            )
        }
    }

    interface View {
        fun showDateString(dateString: String, timeInMills: Long)
        fun showScheduleData(
            selectedDate: Date,
            scheduleMainResponse: ScheduleMainResponse
        )
    }

    interface Presenter {
        fun showSchedulesByDate(date: Date)
    }
}
package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.schedule.ScheduleData
import java.util.*

class ScheduleContract {
    interface Model {
        fun fetchSchedules(
            selectedDate: Date,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(
                selectedDate: Date,
                scheduleDataList: ArrayList<ScheduleData>
            )
        }
    }

    interface View {
        fun showDateString(dateString: String)
        fun showScheduleData(
            selectedDate: Date,
            scheduleDataList: ArrayList<ScheduleData>
        )

        interface OnAdapterItemClickListener {
            fun onWorkItemClick()
            fun onLumperImagesClick()
        }
    }

    interface Presenter {
        fun showSchedulesByDate(date: Date)
    }
}
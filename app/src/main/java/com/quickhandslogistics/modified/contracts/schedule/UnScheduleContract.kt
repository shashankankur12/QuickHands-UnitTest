package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.schedule.ScheduleData
import java.util.*

class UnScheduleContract {
    interface Model {
        fun fetchUnScheduleWork(
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(unScheduledData: ArrayList<ScheduleData>)
        }
    }
    interface View {
        fun showUnScheduleData(unScheduledData: ArrayList<ScheduleData>)
        interface OnAdapterItemClickListener {
            fun onAddLumperItemClick()
            fun onWorkItemClick()
            fun onLumperImagesClick()
        }
    }

    interface Presenter {
        fun showUnScheduleWork()
    }
}
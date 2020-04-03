package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.schedule.ScheduleAPIResponse
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import java.util.*

class ScheduleContract {
    interface Model {
        fun fetchSchedulesByDate(
            selectedDate: Date,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(
                selectedDate: Date,
                scheduleAPIResponse: ScheduleAPIResponse
            )
        }
    }

    interface View {
        fun showDateString(dateString: String)
        fun showScheduleData(
            selectedDate: Date,
            workItemsList: ArrayList<ScheduleDetail>
        )

        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun fetchUnsScheduledWorkItems()
        fun showEmptyData()

        interface OnAdapterItemClickListener {
            fun onScheduleItemClick()
            fun onLumperImagesClick()
        }
    }

    interface Presenter {
        fun getScheduledWorkItemsByDate(date: Date)
    }
}
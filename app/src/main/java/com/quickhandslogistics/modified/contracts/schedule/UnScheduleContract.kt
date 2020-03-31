package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.schedule.ScheduleAPIResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import java.util.*

class UnScheduleContract {
    interface Model {
        fun fetchUnSchedulesByDate(selectedDate: Date, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(
                selectedDate: Date,
                unScheduleAPIResponse: ScheduleAPIResponse
            )
        }
    }
    interface View {
        fun showUnScheduleData(
            selectedDate: Date,
            workItemsList: ArrayList<WorkItemDetail>
        )
        fun hideProgressDialog()

        interface OnAdapterItemClickListener {
            fun onAddLumperItemClick()
            fun onWorkItemClick()
            fun onLumperImagesClick()
        }
    }

    interface Presenter {
        fun getUnScheduledWorkItems(date: Date)
    }
}
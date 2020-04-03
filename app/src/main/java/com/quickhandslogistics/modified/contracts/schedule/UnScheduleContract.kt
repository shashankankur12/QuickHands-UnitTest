package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.schedule.ScheduleAPIResponse
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import java.util.*

class UnScheduleContract {
    interface Model {
        fun fetchUnSchedulesByDate(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(unScheduleAPIResponse: ScheduleAPIResponse)
        }
    }

    interface View {
        fun showUnScheduleData(workItemsList: ArrayList<ScheduleDetail>)
        fun hideProgressDialog()
        fun showProgressDialog(message: String)

        interface OnAdapterItemClickListener {
            fun onUnScheduleItemClick(scheduleDetail: ScheduleDetail)
            fun onLumperImagesClick()
        }
    }

    interface Presenter {
        fun getUnScheduledWorkItems(showProgressDialog: Boolean)
    }
}
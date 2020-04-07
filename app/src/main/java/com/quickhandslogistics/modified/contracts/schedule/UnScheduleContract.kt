package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleListAPIResponse
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import kotlin.collections.ArrayList

class UnScheduleContract {
    interface Model {
        fun fetchUnSchedulesByDate(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(unScheduleListAPIResponse: ScheduleListAPIResponse)
        }
    }

    interface View {
        fun showUnScheduleData(workItemsList: ArrayList<ScheduleDetail>)
        fun hideProgressDialog()
        fun showProgressDialog(message: String)

        interface OnAdapterItemClickListener {
            fun onUnScheduleItemClick(scheduleDetail: ScheduleDetail)
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
        }
    }

    interface Presenter {
        fun getUnScheduledWorkItems(showProgressDialog: Boolean)
    }
}
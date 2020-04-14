package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.data.schedule.UnScheduleListAPIResponse

class UnScheduleContract {
    interface Model {
        fun fetchUnSchedulesByDate(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(unScheduleListAPIResponse: UnScheduleListAPIResponse)
        }
    }

    interface View {
        fun showUnScheduleData(workItemsList: ArrayList<ScheduleDetail>)
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showEmptyData()
        fun showAPIErrorMessage(string: String)

        interface OnAdapterItemClickListener {
            fun onUnScheduleItemClick(scheduleDetail: ScheduleDetail)
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
        }
    }

    interface Presenter {
        fun getUnScheduledWorkItems(showProgressDialog: Boolean)
    }
}
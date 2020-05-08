package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.data.schedule.UnScheduleListAPIResponse

class UnScheduleContract {
    interface Model {
        fun fetchUnSchedulesByDate(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(unScheduleListAPIResponse: UnScheduleListAPIResponse)
        }
    }

    interface View : BaseContract.View {
        fun showUnScheduleData(workItemsList: ArrayList<ScheduleDetail>)
        fun showEmptyData()
        fun showAPIErrorMessage(message: String)

        interface OnAdapterItemClickListener {
            fun onUnScheduleItemClick(scheduleDetail: ScheduleDetail)
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getUnScheduledWorkItems(showProgressDialog: Boolean)
    }
}
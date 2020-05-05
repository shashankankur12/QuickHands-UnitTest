package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.data.schedule.ScheduleDetailAPIResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail

class UnScheduleDetailContract {
    interface Model {
        fun fetchScheduleDetail(
            scheduleIdentityId: String,
            scheduleFromDate: String,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(
                scheduleDetailAPIResponse: ScheduleDetailAPIResponse,
                scheduleFromDate: String
            )
        }
    }

    interface View : BaseContract.View {
        fun showScheduleData(scheduleDetail: ScheduleDetail, scheduleDate: String?)
        fun showAPIErrorMessage(message: String)

        interface OnAdapterItemClickListener {
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
            fun onWorkItemClick(
                workItemDetail: WorkItemDetail,
                workItemTypeDisplayName: String
            )

            fun onAddLumpersItemClick(workItemDetail: WorkItemDetail)
        }
    }

    interface Presenter {
        fun getScheduleDetail(scheduleIdentityId: String, scheduleFromDate: String)
    }
}
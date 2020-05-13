package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.data.schedule.ScheduleDetailAPIResponse

class UnScheduleDetailContract {
    interface Model {
        fun fetchScheduleDetail(scheduleIdentityId: String, scheduleFromDate: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(scheduleDetailAPIResponse: ScheduleDetailAPIResponse, scheduleFromDate: String)
        }
    }

    interface View : BaseContract.View {
        fun showScheduleData(scheduleDetail: ScheduleDetail, scheduleDate: String?)
        fun showAPIErrorMessage(message: String)

        interface OnAdapterItemClickListener {
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getScheduleDetail(scheduleIdentityId: String, scheduleFromDate: String)
    }
}
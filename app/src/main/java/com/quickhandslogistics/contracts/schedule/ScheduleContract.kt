package com.quickhandslogistics.contracts.schedule

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.ScheduleDetail
import com.quickhandslogistics.data.schedule.ScheduleListAPIResponse
import java.util.*

class ScheduleContract {
    interface Model {
        fun fetchSchedulesByDate(selectedDate: Date, pageIndex: Int, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(selectedDate: Date, scheduleListAPIResponse: ScheduleListAPIResponse, currentPageIndex: Int)
        }
    }

    interface View : BaseContract.View {
        fun showDateString(dateString: String)
        fun showScheduleData(selectedDate: Date, workItemsList: ArrayList<ScheduleDetail>, totalPagesCount: Int, nextPageIndex: Int, currentPageIndex: Int)
        fun showAPIErrorMessage(message: String)
        fun showEmptyData()

        interface OnAdapterItemClickListener {
            fun onScheduleItemClick(scheduleDetail: ScheduleDetail)
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getScheduledWorkItemsByDate(date: Date, pageIndex: Int)
    }
}
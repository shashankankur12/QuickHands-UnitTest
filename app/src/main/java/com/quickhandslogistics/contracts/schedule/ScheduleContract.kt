package com.quickhandslogistics.contracts.schedule

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.ScheduleDetailData
import com.quickhandslogistics.data.schedule.ScheduleListAPIResponse
import java.util.*
import kotlin.collections.ArrayList

class ScheduleContract {
    interface Model {
        fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun fetchSchedulesByDate(selectedDate: Date, pageIndex: Int, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(
                selectedDate: Date,
                scheduleListAPIResponse: ScheduleListAPIResponse,
                currentPageIndex: Int,
                deptDetail: String
            )
            fun onSuccessGetHeaderInfo(dateString: String)
        }
    }

    interface View : BaseContract.View {
        fun showDateString(dateString: String)
        fun showScheduleData(selectedDate: Date, workItemsList: ArrayList<ScheduleDetailData>, totalPagesCount: Int, nextPageIndex: Int, currentPageIndex: Int)
        fun showAPIErrorMessage(message: String)
        fun showEmptyData()
        fun showLoginScreen()

        interface OnAdapterItemClickListener {
            fun onScheduleItemClick(scheduleDetail: ScheduleDetailData)
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getScheduledWorkItemsByDate(date: Date, pageIndex: Int)
    }
}
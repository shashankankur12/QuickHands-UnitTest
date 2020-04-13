package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleListAPIResponse
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import java.util.*
import kotlin.collections.ArrayList

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
                scheduleListAPIResponse: ScheduleListAPIResponse
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
            fun onScheduleItemClick(scheduleDetail: ScheduleDetail)
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
        }
    }

    interface Presenter {
        fun getScheduledWorkItemsByDate(date: Date)
    }
}
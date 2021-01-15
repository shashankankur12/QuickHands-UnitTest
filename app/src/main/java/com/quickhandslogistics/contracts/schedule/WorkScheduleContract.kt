package com.quickhandslogistics.contracts.schedule

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.schedule.ScheduleDetail
import com.quickhandslogistics.data.schedule.ScheduleDetailAPIResponse
import java.util.*

class WorkScheduleContract {
    interface Model {
        fun fetchHeaderInfo(
            onFinishedListener1: Date,
            onFinishedListener: OnFinishedListener
        )
        fun fetchScheduleDetail(scheduleIdentityId: String, selectedDate: Date, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessFetchWorkSheet(scheduleDetailAPIResponse: ScheduleDetailAPIResponse)
            fun onSuccessGetHeaderInfo(companyName: String, date: String, shift:String, dept: String)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showWorkSheets(scheduleDetailAPIResponse: ScheduleDetail?)
        fun showHeaderInfo(companyName: String, date: String, shift: String, dept: String)
        fun showLoginScreen()

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }

        interface OnFragmentInteractionListener {
            fun fetchWorkSheetList()
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchWorkSheetList(scheduleIdentityId: String, selectedDate: Date)
    }
}
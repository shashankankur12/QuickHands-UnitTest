package com.quickhandslogistics.contracts.lumperSheet

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.data.schedule.GetPastFutureDateResponse
import com.quickhandslogistics.data.schedule.PastFutureDates
import java.util.*


class LumperSheetContract {
    interface Model {
        fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun fetchLumperSheetList(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun submitLumperSheet(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun fetchPastFutureDate(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: LumperSheetListAPIResponse, selectedDate: Date)
            fun onSuccessSubmitLumperSheet()
            fun onSuccessGetHeaderInfo(dateString: String, shift: String, dept: String)
            fun onSuccessPastFutureDate(response: GetPastFutureDateResponse?)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showDateString(dateString: String, shift: String, dept: String)
        fun showLumperSheetData(lumperInfoList: ArrayList<LumpersInfo>, sheetSubmitted: Boolean, selectedDate: Date, tempLumperIds: ArrayList<String>)
        fun sheetSubmittedSuccessfully()
        fun showLoginScreen()
        fun showPastFutureDate(message: ArrayList<PastFutureDates>)

        interface OnAdapterItemClickListener {
            fun onItemClick(lumperInfo: LumpersInfo)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getLumpersSheetByDate(selectedDate: Date)
        fun initiateSheetSubmission(selectedDate: Date)
    }
}
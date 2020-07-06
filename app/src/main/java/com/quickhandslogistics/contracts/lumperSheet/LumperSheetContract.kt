package com.quickhandslogistics.contracts.lumperSheet

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.data.lumperSheet.LumpersInfo
import java.util.*
import kotlin.collections.ArrayList


class LumperSheetContract {
    interface Model {
        fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun fetchLumperSheetList(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun submitLumperSheet(selectedDate: Date, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: LumperSheetListAPIResponse, selectedDate: Date)
            fun onSuccessSubmitLumperSheet()
            fun onSuccessGetHeaderInfo(dateString: String)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showDateString(dateString: String)
        fun showLumperSheetData(lumperInfoList: ArrayList<LumpersInfo>, sheetSubmitted: Boolean, selectedDate: Date, tempLumperIds: ArrayList<String>)
        fun sheetSubmittedSuccessfully()

        interface OnAdapterItemClickListener {
            fun onItemClick(lumperInfo: LumpersInfo)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getLumpersSheetByDate(selectedDate: Date)
        fun initiateSheetSubmission(selectedDate: Date)
    }
}
package com.quickhandslogistics.modified.contracts.lumperSheet

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumperSheet.LumperDaySheet
import com.quickhandslogistics.modified.data.lumperSheet.LumperWorkDetailAPIResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import java.util.*

class LumperWorkDetailContract {
    interface Model {
        fun fetchLumperWorkDetails(lumperId: String, selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun saveLumperSignature(lumperId: String, date: Date, signatureFilePath: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: LumperWorkDetailAPIResponse)
            fun onSuccessSaveLumperSignature(lumperId: String, date: Date)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumperWorkDetails(lumperDaySheetList: ArrayList<LumperDaySheet>)
        fun lumperSignatureSaved()

        interface OnAdapterItemClickListener {
            fun onBOItemClick(workItemDetail: WorkItemDetail)
            fun onNotesItemClick(notes: String?)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getLumperWorkDetails(lumperId: String, selectedDate: Date)
        fun saveLumperSignature(lumperId: String, date: Date, signatureFilePath: String)
    }
}
package com.quickhandslogistics.modified.contracts.scheduleTime

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.scheduleTime.RequestLumpersListAPIResponse
import com.quickhandslogistics.modified.data.scheduleTime.RequestLumpersRecord
import java.util.*

class RequestLumpersContract {
    interface Model {
        fun fetchAllRequestsByDate(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun createNewRequestForLumpers(requiredLumperCount: String, notesDM: String, date: Date, onFinishedListener: OnFinishedListener)
        fun updateRequestForLumpers(requestId: String, requiredLumperCount: String, notesDM: String, date: Date, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessFetchRequests(response: RequestLumpersListAPIResponse)
            fun onSuccessRequest(date: Date)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showAllRequests(records: List<RequestLumpersRecord>)
        fun showHeaderInfo(dateString: String)
        fun showSuccessDialog(date: Date)

        interface OnAdapterItemClickListener {
            fun onNotesItemClick(notes: String?)
            fun onUpdateItemClick(record: RequestLumpersRecord)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchAllRequestsByDate(selectedDate: Date)
        fun createNewRequestForLumpers(requiredLumperCount: String, notesDM: String, date: Date)
        fun updateRequestForLumpers(requestId: String, requiredLumperCount: String, notesDM: String, date: Date)
    }
}
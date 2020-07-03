package com.quickhandslogistics.contracts.scheduleTime

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.scheduleTime.RequestLumpersListAPIResponse
import com.quickhandslogistics.data.scheduleTime.RequestLumpersRecord
import java.util.*
import kotlin.collections.ArrayList

class RequestLumpersContract {
    interface Model {
        fun fetchAllRequestsByDate(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun createNewRequestForLumpers(requiredLumperCount: String, notesDM: String, date: Date, onFinishedListener: OnFinishedListener)
        fun cancelRequestForLumpers(requestId: String, date: Date, onFinishedListener: OnFinishedListener)
        fun updateRequestForLumpers(requestId: String, requiredLumperCount: String, notesDM: String, date: Date, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessCancelRequest(date: Date)
            fun onSuccessFetchRequests(response: RequestLumpersListAPIResponse)
            fun onSuccessRequest(date: Date)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showAllRequests(records: ArrayList<RequestLumpersRecord>)
        fun showHeaderInfo(dateString: String)
        fun showSuccessDialog(message:String, date: Date)

        interface OnAdapterItemClickListener {
            fun onNotesItemClick(notes: String?)
            fun onUpdateItemClick(record: RequestLumpersRecord)
            fun onCancelItemClick(record: RequestLumpersRecord)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchAllRequestsByDate(selectedDate: Date)
        fun createNewRequestForLumpers(requiredLumperCount: String, notesDM: String, date: Date)
        fun cancelRequestForLumpers(requestId: String, date: Date)
        fun updateRequestForLumpers(requestId: String, requiredLumperCount: String, notesDM: String, date: Date)
    }
}
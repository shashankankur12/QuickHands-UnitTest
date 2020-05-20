package com.quickhandslogistics.modified.contracts.scheduleTime

import com.quickhandslogistics.modified.contracts.BaseContract
import java.util.*

class RequestLumpersContract {
    interface Model {
        fun assignScheduleTime(
            scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, requiredLumpersCount: Int,
            notesDM: String, selectedDate: Date, onFinishedListener: OnFinishedListener
        )
        fun fetchAllRequestsByDate(selectedDate: Date, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessScheduleTime()
            fun onSuccessFetchRequests()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showAllRequests()

        interface OnAdapterItemClickListener {
            fun onUpdateClick(adapterPosition: Int)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun initiateScheduleTime(scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, requiredLumpersCount: Int, notesDM: String, selectedDate: Date)
        fun fetchAllRequestsByDate(selectedDate: Date)
    }
}
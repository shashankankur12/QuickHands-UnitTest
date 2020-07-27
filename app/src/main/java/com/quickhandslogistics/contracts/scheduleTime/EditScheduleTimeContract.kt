package com.quickhandslogistics.contracts.scheduleTime

import com.quickhandslogistics.contracts.BaseContract
import java.util.*

class EditScheduleTimeContract {
    interface Model {
        fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun assignScheduleTime(
            scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, selectedDate: Date, onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessScheduleTime()
            fun onSuccessGetHeaderInfo(dateString: String)
        }
    }

    interface View : BaseContract.View {
        fun showDateString(dateString: String)
        fun showAPIErrorMessage(message: String)
        fun scheduleTimeFinished()
        fun showLoginScreen()

        interface OnAdapterItemClickListener {
            fun onAddStartTimeClick(adapterPosition: Int, timeInMillis: Long)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getHeaderDateString(date: Date)
        fun initiateScheduleTime(scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, selectedDate: Date)
    }
}
package com.quickhandslogistics.modified.contracts.scheduleTime

import com.quickhandslogistics.modified.contracts.BaseContract
import java.util.*

class EditScheduleTimeContract {
    interface Model {
        fun assignScheduleTime(
            scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, selectedDate: Date, onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessScheduleTime()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun scheduleTimeFinished()

        interface OnAdapterItemClickListener {
            fun onAddStartTimeClick(adapterPosition: Int, timeInMillis: Long)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun initiateScheduleTime(scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, selectedDate: Date)
    }
}
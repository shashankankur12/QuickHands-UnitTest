package com.quickhandslogistics.contracts.scheduleTime

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import java.util.*

class EditScheduleTimeContract {
    interface Model {
        fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun assignScheduleTime(
            scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, selectedDate: Date, onFinishedListener: OnFinishedListener
        )
        fun cancelScheduleLumpers(
            lumperId: String,
            date: Date,
            position: Int,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessScheduleTime()
            fun onSuccessGetHeaderInfo(dateString: String)
            fun onSuccessRequest(position: Int)
        }
    }

    interface View : BaseContract.View {
        fun showDateString(dateString: String)
        fun showAPIErrorMessage(message: String)
        fun scheduleTimeFinished()
        fun showLoginScreen()
        fun showSuccessDialog(message:String, position: Int)

        interface OnAdapterItemClickListener {
            fun onAddStartTimeClick(adapterPosition: Int, timeInMillis: Long)
            fun onAddScheduleNoteClick(adapterPosition: Int)
            fun onAddRemoveClick(
                adapterPosition: Int,
                item: ScheduleTimeDetail
            )
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getHeaderDateString(date: Date)
        fun cancelScheduleLumpers(
            lumperId: String,
            date: Date,
            position: Int
        )
        fun initiateScheduleTime(scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, selectedDate: Date)
    }
}
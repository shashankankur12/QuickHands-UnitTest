package com.quickhandslogistics.modified.contracts.scheduleTime

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.common.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.lumpers.LumperListAPIResponse
import java.util.*

class EditScheduleTimeContract {
    interface Model {
        fun assignScheduleTime(
            scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, requiredLumpersCount: Int,
            notesDM: String, selectedDate: Date, onFinishedListener: OnFinishedListener
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
        fun initiateScheduleTime(
            scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, requiredLumpersCount: Int, notesDM: String, selectedDate: Date
        )
    }
}
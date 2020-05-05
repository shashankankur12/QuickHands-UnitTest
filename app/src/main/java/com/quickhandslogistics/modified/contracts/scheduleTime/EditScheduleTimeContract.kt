package com.quickhandslogistics.modified.contracts.scheduleTime

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import java.util.*

class EditScheduleTimeContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)
        fun assignScheduleTime(
            scheduledLumpersIdsTimeMap: HashMap<String, Long>,
            notes: String, requiredLumpersCount: Int, notesDM: String,
            selectedDate: Date, onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccessFetchLumpers(allLumpersResponse: AllLumpersResponse)
            fun onSuccessScheduleTime()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>)
        fun scheduleTimeFinished()

        interface OnAdapterItemClickListener {
            fun onAddStartTimeClick(adapterPosition: Int, timeInMillis: Long)
        }
    }

    interface Presenter {
        fun fetchLumpersList()
        fun onDestroy()
        fun initiateScheduleTime(
            scheduledLumpersIdsTimeMap: HashMap<String, Long>,
            notes: String, requiredLumpersCount: Int, notesDM: String, selectedDate: Date
        )
    }
}
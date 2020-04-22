package com.quickhandslogistics.modified.contracts.scheduleTime

import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import java.util.*
import kotlin.collections.ArrayList

class ScheduleTimeContract {
    interface Model {
        fun fetchSchedulesTimeByDate(
            selectedDate: Date,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(
                selectedDate: Date,
                allLumpersResponse: AllLumpersResponse
            )
        }
    }

    interface View {
        fun showDateString(dateString: String)
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun showScheduleTimeData(selectedDate: Date, employeeDataList: ArrayList<EmployeeData>)
    }

    interface Presenter {
        fun getSchedulesTimeByDate(date: Date)
        fun onDestroy()
    }
}
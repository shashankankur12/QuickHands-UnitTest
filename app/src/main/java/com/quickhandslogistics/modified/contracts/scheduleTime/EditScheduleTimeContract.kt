package com.quickhandslogistics.modified.contracts.scheduleTime

import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class EditScheduleTimeContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)
        fun assignScheduleTime(
            workItemId: String,
            workItemType: String,
            selectedLumperIdsList: ArrayList<String>,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccessFetchLumpers(allLumpersResponse: AllLumpersResponse)
            fun onSuccessScheduleTime()
        }
    }

    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>)
        fun scheduleTimeFinished()

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
            fun onAddStartTimeClick(adapterPosition: Int, timeInMillis: Long)
        }
    }

    interface Presenter {
        fun fetchLumpersList()
        fun onDestroy()
        fun initiateScheduleTime(
            selectedLumperIdsList: ArrayList<String>,
            workItemId: String,
            workItemType: String
        )
    }
}
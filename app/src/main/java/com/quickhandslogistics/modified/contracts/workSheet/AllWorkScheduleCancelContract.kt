package com.quickhandslogistics.modified.contracts.workSheet

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class AllWorkScheduleCancelContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)
        fun cancelAllWorkSchedules(
            selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String, onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessFetchLumpers(allLumpersResponse: AllLumpersResponse)
            fun onSuccessCancelWorkSchedules()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>)
        fun cancellingWorkScheduleFinished()

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchLumpersList()
        fun initiateCancellingWorkSchedules(selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String)
    }
}
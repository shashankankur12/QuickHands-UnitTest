package com.quickhandslogistics.modified.contracts.workSheet

import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class AllWorkScheduleCancelContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)
        fun cancelAllWorkSchedules(
            selectedLumperIdsList: ArrayList<String>,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccessFetchLumpers(allLumpersResponse: AllLumpersResponse)
            fun onSuccessCancelWorkSchedules()
        }
    }

    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>)
        fun cancellingWorkScheduleFinished()

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }
    }

    interface Presenter {
        fun fetchLumpersList()
        fun onDestroy()
        fun initiateCancellingWorkSchedules(selectedLumperIdsList: ArrayList<String>)
    }
}
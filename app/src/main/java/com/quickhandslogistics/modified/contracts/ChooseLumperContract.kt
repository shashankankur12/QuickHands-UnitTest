package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class ChooseLumperContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(allLumpersResponse: AllLumpersResponse)
        }
    }

    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>)

        interface OnAdapterItemClickListener {
            fun onClickLumperDetail(employeeData: EmployeeData)
            fun onSelectLumper(employeeData: EmployeeData)
        }
    }

    interface Presenter {
        fun fetchLumpersList()
        fun onDestroy()
    }
}
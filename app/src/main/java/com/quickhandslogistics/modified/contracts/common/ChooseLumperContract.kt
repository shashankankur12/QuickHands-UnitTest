package com.quickhandslogistics.modified.contracts.common

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class ChooseLumperContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(allLumpersResponse: AllLumpersResponse)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>)

        interface OnAdapterItemClickListener {
            fun onClickLumperDetail(employeeData: EmployeeData)
            fun onSelectLumper(employeeData: EmployeeData)
            fun onPhoneViewClick(lumperName: String, phone: String)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchLumpersList()
    }
}
package com.quickhandslogistics.contracts.reports

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse

class LumperJobHistoryContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: LumperListAPIResponse)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>)

        interface OnAdapterItemClickListener {
            fun onSelectLumper()
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchLumpersList()
    }
}
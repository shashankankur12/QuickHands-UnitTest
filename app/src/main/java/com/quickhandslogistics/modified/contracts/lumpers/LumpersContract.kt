package com.quickhandslogistics.modified.contracts.lumpers

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.lumpers.LumperListAPIResponse

class LumpersContract {
    interface Model {
        fun fetchLumpersList(pageIndex: Int, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: LumperListAPIResponse, currentPageIndex: Int)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>, totalPagesCount: Int, nextPageIndex: Int, currentPageIndex: Int)

        interface OnAdapterItemClickListener {
            fun onItemClick(employeeData: EmployeeData)
            fun onPhoneViewClick(lumperName: String, phone: String)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchLumpersList(currentPageIndex: Int)
    }
}
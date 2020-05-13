package com.quickhandslogistics.modified.contracts.common

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.lumpers.LumperListAPIResponse

class ChooseLumpersContract {
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
            fun onSelectLumper(totalSelectedCount: Int)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchLumpersList(pageIndex: Int)
    }
}
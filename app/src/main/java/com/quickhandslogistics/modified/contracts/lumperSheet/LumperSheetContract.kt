package com.quickhandslogistics.modified.contracts.lumperSheet

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import java.util.*


class LumperSheetContract {
    interface Model {
        fun fetchLumperSheetList(selectedDate: Date, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(response: AllLumpersResponse, selectedDate: Date)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showDateString(dateString: String)
        fun showLumperSheetData(employeeDataList: ArrayList<EmployeeData>)

        interface OnAdapterItemClickListener {
            fun onItemClick(employeeData: EmployeeData)
        }
    }

    interface Presenter {
        fun getLumpersSheetByDate(selectedDate: Date)
        fun onDestroy()
    }
}
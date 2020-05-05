package com.quickhandslogistics.modified.contracts.lumperSheet

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumperSheet.LumperModel
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList


class LumperSheetContract {
    interface Model {
        fun fetchLumperSheetList(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(response: AllLumpersResponse)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showDateString(dateString: String)
        fun showLumperSheetData(employeeDataList: ArrayList<EmployeeData>)

        interface OnAdapterItemClickListener {
            fun onItemClick(employeeData: EmployeeData)
            fun onAddSignatureItemClick(position: Int)
        }
    }

    interface Presenter {
        fun getLumpersSheetByDate(date: Date)
        fun onDestroy()
    }
}
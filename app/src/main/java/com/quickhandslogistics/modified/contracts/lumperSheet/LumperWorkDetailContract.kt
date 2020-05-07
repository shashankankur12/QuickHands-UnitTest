package com.quickhandslogistics.modified.contracts.lumperSheet

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import java.util.*

class LumperWorkDetailContract {
    interface Model {
        fun fetchLumperWorkDetails(lumperId: String?, selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun saveLumperSignature(lumperId: String, date: Date, signatureFilePath: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(response: AllLumpersResponse)
            fun onSuccessSaveLumperSignature(lumperId: String, date: Date)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumperWorkDetails(employeeDataList: ArrayList<EmployeeData>)

        interface OnAdapterItemClickListener {
            fun onBOItemClick(buildingOps: HashMap<String, String>, parameters: ArrayList<String>)
            fun onNotesItemClick(notes: String?)
        }
    }

    interface Presenter {
        fun getLumperWorkDetails(lumperId: String?, selectedDate: Date)
        fun saveLumperSignature(lumperId: String, date: Date, signatureFilePath: String)
        fun onDestroy()
    }
}
package com.quickhandslogistics.contracts.lumperSheet

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.lumperSheet.LumperDaySheet
import com.quickhandslogistics.data.lumperSheet.LumperWorkDetailAPIResponse
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.lumperSheet.LumperCorrectionRequest
import java.util.*
import kotlin.collections.ArrayList

class LumperWorkDetailContract {
    interface Model {
        fun fetchLumperWorkDetails(lumperId: String, selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun saveLumperSignature(lumperId: String, date: Date, signatureFilePath: String, onFinishedListener: OnFinishedListener)
        fun saveLumpersAttendanceList(attendanceDetailList: List<AttendanceDetail>, onFinishedListener: OnFinishedListener)
        fun sendCorrectionRequest(request: LumperCorrectionRequest, containerId: String, onFinishedListener: OnFinishedListener)
        fun editLumperParamsRequest(request: LumperCorrectionRequest, containerId: String, onFinishedListener: OnFinishedListener)
        fun cancelCorrectionRequest(status: String, containerId: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: LumperWorkDetailAPIResponse)
            fun onSuccessSaveLumperSignature(lumperId: String, date: Date)
            fun onSuccessSaveDate()
            fun onSuccessRequestCorrection(message: String?)
            fun onSuccessCancelCorrection(message: String?)

        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumperWorkDetails(
            lumperDaySheetList: ArrayList<LumperDaySheet>,
            lumperAttendanceData: AttendanceDetail
        )
        fun lumperSignatureSaved()
        fun showDataSavedMessage()
        fun showSuccessCorrection(message: String)
        fun showLoginScreen()

        interface OnAdapterItemClickListener {
            fun onBOItemClick(workItemDetail: WorkItemDetail, parameters: ArrayList<String>)
            fun onNotesItemClick(notes: String?)
            fun requestCorrection(lumperDaySheet: LumperDaySheet)
            fun cancelRequestCorrection(id: String)
            fun updateRequestCorrection(correctionRequest: LumperDaySheet)
            fun editLumperParams(lumperDaySheet: LumperDaySheet)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getLumperWorkDetails(lumperId: String, selectedDate: Date)
        fun saveLumperSignature(lumperId: String, date: Date, signatureFilePath: String)
        fun saveAttendanceDetails(attendanceDetailList: List<AttendanceDetail>)
        fun sendCorrectionRequest(request: LumperCorrectionRequest, containerId: String)
        fun editLumperParamsRequest(request: LumperCorrectionRequest, containerId: String)
        fun cancelCorrectionRequest(status: String, containerId: String)
    }
}
package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.modified.data.attendance.LumperAttendanceData

class MarkAttendanceContract {
    interface Model {
        fun fetchLumpersAttendanceList(onFinishedListener: OnFinishedListener)
        fun saveLumpersAttendanceList(
            attendanceDetailList: List<AttendanceDetail>,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccessGetList(attendanceAPIResponse: GetAttendanceAPIResponse)
            fun onSuccessSaveDate()
        }
    }

    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun showLumpersAttendance(lumperAttendanceList: ArrayList<LumperAttendanceData>)
        fun showDataSavedMessage()

        interface OnAdapterItemClickListener {
            fun onAddTimeClick(
                lumperAttendanceData: LumperAttendanceData,
                itemPosition: Int
            )
            fun onAddNotes(updatedDataSize: Int)
        }
    }

    interface Presenter {
        fun fetchAttendanceList()
        fun saveAttendanceDetails(attendanceDetailList: List<AttendanceDetail>)
        fun onDestroy()
    }
}
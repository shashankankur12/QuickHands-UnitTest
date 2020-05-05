package com.quickhandslogistics.modified.contracts.attendance

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.modified.data.attendance.LumperAttendanceData

class TimeClockAttendanceContract {
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

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersAttendance(lumperAttendanceList: ArrayList<LumperAttendanceData>)
        fun showDataSavedMessage()

        interface OnAdapterItemClickListener {
            fun onAddTimeClick(
                lumperAttendanceData: LumperAttendanceData,
                itemPosition: Int
            )

            fun onAddNotes(updatedDataSize: Int)
            fun onRowLongClicked(itemPosition: Int)
            fun onRowClicked(itemPosition: Int)
        }
    }

    interface Presenter {
        fun fetchAttendanceList()
        fun saveAttendanceDetails(attendanceDetailList: List<AttendanceDetail>)
        fun onDestroy()
    }
}
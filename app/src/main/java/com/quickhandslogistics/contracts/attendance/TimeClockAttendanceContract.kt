package com.quickhandslogistics.contracts.attendance

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.data.attendance.LumperAttendanceData

class TimeClockAttendanceContract {
    interface Model {
        fun fetchLumpersAttendanceList(onFinishedListener: OnFinishedListener)
        fun saveLumpersAttendanceList(attendanceDetailList: List<AttendanceDetail>, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessGetList(response: GetAttendanceAPIResponse)
            fun onSuccessSaveDate()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersAttendance(lumperAttendanceList: ArrayList<LumperAttendanceData>)
        fun showDataSavedMessage()

        interface OnAdapterItemClickListener {
            fun onAddTimeClick(lumperAttendanceData: LumperAttendanceData, itemPosition: Int)
            fun onAddNotes(updatedDataSize: Int)
            fun onRowLongClicked(itemPosition: Int)
            fun onRowClicked(itemPosition: Int)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchAttendanceList()
        fun saveAttendanceDetails(attendanceDetailList: List<AttendanceDetail>)
    }
}
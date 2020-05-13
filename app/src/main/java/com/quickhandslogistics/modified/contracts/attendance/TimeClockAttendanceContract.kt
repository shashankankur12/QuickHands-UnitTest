package com.quickhandslogistics.modified.contracts.attendance

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.modified.data.attendance.LumperAttendanceData

class TimeClockAttendanceContract {
    interface Model {
        fun fetchLumpersAttendanceList(pageIndex: Int, onFinishedListener: OnFinishedListener)
        fun saveLumpersAttendanceList(attendanceDetailList: List<AttendanceDetail>, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessGetList(attendanceAPIResponse: GetAttendanceAPIResponse, currentPageIndex: Int)
            fun onSuccessSaveDate()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersAttendance(lumperAttendanceList: ArrayList<LumperAttendanceData>, totalPagesCount: Int, nextPageIndex: Int, currentPageIndex: Int)
        fun showDataSavedMessage()

        interface OnAdapterItemClickListener {
            fun onAddTimeClick(lumperAttendanceData: LumperAttendanceData, itemPosition: Int)
            fun onAddNotes(updatedDataSize: Int)
            fun onRowLongClicked(itemPosition: Int)
            fun onRowClicked(itemPosition: Int)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchAttendanceList(pageIndex: Int)
        fun saveAttendanceDetails(attendanceDetailList: List<AttendanceDetail>)
    }
}
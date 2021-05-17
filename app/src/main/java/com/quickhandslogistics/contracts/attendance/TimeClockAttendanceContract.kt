package com.quickhandslogistics.contracts.attendance

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import java.util.*
import kotlin.collections.ArrayList

class TimeClockAttendanceContract {
    interface Model {
        fun fetchHeaderInfo(onFinishedListener: OnFinishedListener)
        fun fetchLumpersAttendanceList(selectedTime: Date, onFinishedListener: OnFinishedListener)
        fun saveLumpersAttendanceList(attendanceDetailList: List<AttendanceDetail>, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessGetHeaderInfo(date: String, shift: String, dept: String)
            fun onSuccessGetList(response: GetAttendanceAPIResponse?, selectedTime: Date)
            fun onSuccessSaveDate()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersAttendance(
            lumperAttendanceList: ArrayList<LumperAttendanceData>,
            selectedTime: Date
        )
        fun showDataSavedMessage()
        fun showHeaderInfo(date: String, shift: String, dept: String)
        fun showLoginScreen()

        interface OnAdapterItemClickListener {
            fun onAddTimeClick(lumperAttendanceData: LumperAttendanceData, itemPosition: Int)
            fun onAddNotes(updatedDataSize: Int)
            fun onRowLongClicked(itemPosition: Int)
            fun onRowClicked(itemPosition: Int)
            fun onSaveNote()
        }
        interface fragmentDataListener {
            fun onDataChanges(): Boolean
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchAttendanceList(selectedTime: Date)
        fun saveAttendanceDetails(attendanceDetailList: List<AttendanceDetail>)
    }
}
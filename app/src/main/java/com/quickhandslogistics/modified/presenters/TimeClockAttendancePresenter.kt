package com.quickhandslogistics.modified.presenters

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.TimeClockAttendanceContract
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.modified.models.TimeClockAttendanceModel

class TimeClockAttendancePresenter(
    private var timeClockAttendanceView: TimeClockAttendanceContract.View?,
    private val resources: Resources
) : TimeClockAttendanceContract.Presenter, TimeClockAttendanceContract.Model.OnFinishedListener {

    private val timeClockAttendanceModel: TimeClockAttendanceModel = TimeClockAttendanceModel()

    override fun fetchAttendanceList() {
        timeClockAttendanceView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        timeClockAttendanceModel.fetchLumpersAttendanceList(this)
    }

    override fun saveAttendanceDetails(attendanceDetailList: List<AttendanceDetail>) {
        timeClockAttendanceView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        timeClockAttendanceModel.saveLumpersAttendanceList(attendanceDetailList, this)
    }

    override fun onDestroy() {
        timeClockAttendanceView = null
    }

    override fun onFailure(message: String) {
        timeClockAttendanceView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            timeClockAttendanceView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            timeClockAttendanceView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessGetList(attendanceAPIResponse: GetAttendanceAPIResponse) {
        timeClockAttendanceView?.hideProgressDialog()
        attendanceAPIResponse.data?.let { data ->
            timeClockAttendanceView?.showLumpersAttendance(data)
        }
    }

    override fun onSuccessSaveDate() {
        timeClockAttendanceView?.hideProgressDialog()
        timeClockAttendanceView?.showDataSavedMessage()
    }
}
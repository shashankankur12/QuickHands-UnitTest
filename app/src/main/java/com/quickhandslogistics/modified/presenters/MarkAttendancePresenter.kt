package com.quickhandslogistics.modified.presenters

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.MarkAttendanceContract
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.modified.models.MarkAttendanceModel

class MarkAttendancePresenter(
    private var markAttendanceView: MarkAttendanceContract.View?,
    private val resources: Resources
) : MarkAttendanceContract.Presenter, MarkAttendanceContract.Model.OnFinishedListener {

    private val markAttendanceModel: MarkAttendanceModel = MarkAttendanceModel()

    override fun fetchAttendanceList() {
        markAttendanceView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        markAttendanceModel.fetchLumpersAttendanceList(this)
    }

    override fun saveAttendanceDetails(attendanceDetailList: List<AttendanceDetail>) {
        markAttendanceView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        markAttendanceModel.saveLumpersAttendanceList(attendanceDetailList, this)
    }

    override fun onDestroy() {
        markAttendanceView = null
    }

    override fun onFailure(message: String) {
        markAttendanceView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            markAttendanceView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            markAttendanceView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessGetList(attendanceAPIResponse: GetAttendanceAPIResponse) {
        markAttendanceView?.hideProgressDialog()
        attendanceAPIResponse.data?.let { data ->
            markAttendanceView?.showLumpersAttendance(data)
        }
    }

    override fun onSuccessSaveDate() {
        markAttendanceView?.hideProgressDialog()
        markAttendanceView?.showDataSavedMessage()
    }
}
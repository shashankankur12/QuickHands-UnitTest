package com.quickhandslogistics.modified.presenters.attendance

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.attendance.TimeClockAttendanceContract
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.modified.models.attendance.TimeClockAttendanceModel
import com.quickhandslogistics.utils.ValueUtils

class TimeClockAttendancePresenter(private var timeClockAttendanceView: TimeClockAttendanceContract.View?, private val resources: Resources) :
    TimeClockAttendanceContract.Presenter, TimeClockAttendanceContract.Model.OnFinishedListener {

    private val timeClockAttendanceModel = TimeClockAttendanceModel()

    /** View Listeners */
    override fun onDestroy() {
        timeClockAttendanceView = null
    }

    override fun fetchAttendanceList(pageIndex: Int) {
        timeClockAttendanceView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        timeClockAttendanceModel.fetchLumpersAttendanceList(pageIndex, this)
    }

    override fun saveAttendanceDetails(attendanceDetailList: List<AttendanceDetail>) {
        timeClockAttendanceView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        timeClockAttendanceModel.saveLumpersAttendanceList(attendanceDetailList, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        timeClockAttendanceView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            timeClockAttendanceView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            timeClockAttendanceView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessGetList(response: GetAttendanceAPIResponse, currentPageIndex: Int) {
        timeClockAttendanceView?.hideProgressDialog()

        val totalPagesCount = ValueUtils.getDefaultOrValue(response.data?.pageCount)
        val nextPageIndex = ValueUtils.getDefaultOrValue(response.data?.next)

        response.data?.employeeDataList?.let { employeeDataList ->
            timeClockAttendanceView?.showLumpersAttendance(employeeDataList, totalPagesCount, nextPageIndex, currentPageIndex)
        }
    }

    override fun onSuccessSaveDate() {
        timeClockAttendanceView?.hideProgressDialog()
        timeClockAttendanceView?.showDataSavedMessage()
    }
}
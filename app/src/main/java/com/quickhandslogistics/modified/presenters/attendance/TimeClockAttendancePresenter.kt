package com.quickhandslogistics.modified.presenters.attendance

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.attendance.TimeClockAttendanceContract
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.modified.data.attendance.LumperAttendanceData
import com.quickhandslogistics.modified.models.attendance.TimeClockAttendanceModel

class TimeClockAttendancePresenter(private var timeClockAttendanceView: TimeClockAttendanceContract.View?, private val resources: Resources) :
    TimeClockAttendanceContract.Presenter, TimeClockAttendanceContract.Model.OnFinishedListener {

    private val timeClockAttendanceModel = TimeClockAttendanceModel()

    /** View Listeners */
    override fun onDestroy() {
        timeClockAttendanceView = null
    }

    override fun fetchAttendanceList() {
        timeClockAttendanceView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        timeClockAttendanceModel.fetchLumpersAttendanceList(this)
    }

    override fun saveAttendanceDetails(attendanceDetailList: List<AttendanceDetail>) {
        timeClockAttendanceView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        timeClockAttendanceModel.saveLumpersAttendanceList(attendanceDetailList, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        timeClockAttendanceView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            timeClockAttendanceView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            timeClockAttendanceView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessGetList(response: GetAttendanceAPIResponse) {
        timeClockAttendanceView?.hideProgressDialog()

        val allLumpersList = ArrayList<LumperAttendanceData>()
        allLumpersList.addAll(response.data?.permanentLumpersList!!)
        allLumpersList.addAll(response.data?.temporaryLumpers!!)

        timeClockAttendanceView?.showLumpersAttendance(allLumpersList)
    }

    override fun onSuccessSaveDate() {
        timeClockAttendanceView?.hideProgressDialog()
        timeClockAttendanceView?.showDataSavedMessage()
    }
}
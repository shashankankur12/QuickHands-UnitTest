package com.quickhandslogistics.presenters.attendance

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.attendance.TimeClockAttendanceContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.models.attendance.TimeClockAttendanceModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class TimeClockAttendancePresenter(private var timeClockAttendanceView: TimeClockAttendanceContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    TimeClockAttendanceContract.Presenter, TimeClockAttendanceContract.Model.OnFinishedListener {

    private val timeClockAttendanceModel = TimeClockAttendanceModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        timeClockAttendanceView = null
    }

    override fun fetchAttendanceList() {
        timeClockAttendanceView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        timeClockAttendanceModel.fetchHeaderInfo(this)
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

    override fun onErrorCode(errorCode: ErrorResponse) {
        timeClockAttendanceView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            timeClockAttendanceView?.showLoginScreen()
        }
    }

    override fun onSuccessGetHeaderInfo(date: String) {
        timeClockAttendanceView?.showHeaderInfo(date)
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
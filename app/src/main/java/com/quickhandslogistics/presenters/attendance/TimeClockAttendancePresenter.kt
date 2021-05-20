package com.quickhandslogistics.presenters.attendance

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.attendance.TimeClockAttendanceContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.data.schedule.GetPastFutureDateResponse
import com.quickhandslogistics.models.attendance.TimeClockAttendanceModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.utils.SharedPref
import java.util.*
import kotlin.collections.ArrayList

class TimeClockAttendancePresenter(private var timeClockAttendanceView: TimeClockAttendanceContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    TimeClockAttendanceContract.Presenter, TimeClockAttendanceContract.Model.OnFinishedListener {

    private val timeClockAttendanceModel = TimeClockAttendanceModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        timeClockAttendanceView = null
    }

    override fun fetchAttendanceList(selectedTime: Date) {
        timeClockAttendanceView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        timeClockAttendanceModel.fetchPastFutureDate(this)
        timeClockAttendanceModel.fetchHeaderInfo(this)
        timeClockAttendanceModel.fetchLumpersAttendanceList(selectedTime, this)
    }

    override fun saveAttendanceDetails(attendanceDetailList: List<AttendanceDetail>, date: Date) {
        timeClockAttendanceView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        timeClockAttendanceModel.saveLumpersAttendanceList(attendanceDetailList, date, this)
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

    override fun onSuccessGetHeaderInfo(date: String, shift: String, dept: String) {
        timeClockAttendanceView?.showHeaderInfo(date, shift, dept)
    }

    override fun onSuccessGetList(response: GetAttendanceAPIResponse?, selectedTime: Date) {
        timeClockAttendanceView?.hideProgressDialog()

        val allLumpersList = ArrayList<LumperAttendanceData>()
        response?.data?.permanentLumpersList?.let { allLumpersList.addAll(it) }
        response?.data?.temporaryLumpers?.let { allLumpersList.addAll(it) }

        timeClockAttendanceView?.showLumpersAttendance(ScheduleUtils.getSortedAttendenceData(allLumpersList), selectedTime)
    }

    override fun onSuccessSaveDate() {
        timeClockAttendanceView?.hideProgressDialog()
        timeClockAttendanceView?.showDataSavedMessage()
    }

    override fun onSuccessPastFutureDate(response: GetPastFutureDateResponse?) {
        response?.data?.let {
            timeClockAttendanceView?.showPastFutureDate(it)
        }
    }
}
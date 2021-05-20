package com.quickhandslogistics.models.attendance

import android.util.Log
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.attendance.TimeClockAttendanceContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.schedule.GetPastFutureDateResponse
import com.quickhandslogistics.models.schedule.ScheduleModel
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TimeClockAttendanceModel(private val sharedPref: SharedPref) : TimeClockAttendanceContract.Model {

    override fun fetchHeaderInfo(onFinishedListener: TimeClockAttendanceContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
        val date = DateUtils.getCurrentDateStringByEmployeeShift(pattern = DateUtils.PATTERN_NORMAL)
        val shiftDetail = ScheduleUtils.getShiftDetailString(leadProfile)
        val deptDetail = "${ ResourceManager.getInstance().getString(R.string.dept_bold)} ${ UIUtils.getDisplayEmployeeDepartmentHeader(leadProfile)}"
        onFinishedListener.onSuccessGetHeaderInfo(date, shiftDetail, deptDetail)
    }

    override fun fetchLumpersAttendanceList(selectedTime: Date, onFinishedListener: TimeClockAttendanceContract.Model.OnFinishedListener) {
//        val dateString = DateUtils.getCurrentDateStringByEmployeeShift()
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedTime)
        DataManager.getService().getAttendanceList(getAuthToken(), dateString).enqueue(object : Callback<GetAttendanceAPIResponse> {
            override fun onResponse(call: Call<GetAttendanceAPIResponse>, response: Response<GetAttendanceAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessGetList(response.body(), selectedTime)
                }
            }

            override fun onFailure(call: Call<GetAttendanceAPIResponse>, t: Throwable) {
                Log.e(TimeClockAttendanceModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun saveLumpersAttendanceList(
            attendanceDetailList: List<AttendanceDetail>, date: Date, onFinishedListener: TimeClockAttendanceContract.Model.OnFinishedListener
    ) {
//        val dateString = DateUtils.getCurrentDateStringByEmployeeShift()
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, date)

        DataManager.getService().saveAttendanceDetails(getAuthToken(), dateString, attendanceDetailList).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessSaveDate()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(TimeClockAttendanceModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun fetchPastFutureDate(onFinishedListener: TimeClockAttendanceContract.Model.OnFinishedListener) {
        DataManager.getService().timeClockPastFutureDate(getAuthToken()).enqueue(object : Callback<GetPastFutureDateResponse> {
            override fun onResponse(call: Call<GetPastFutureDateResponse>, response: Response<GetPastFutureDateResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessPastFutureDate(response.body())
                }
            }

            override fun onFailure(call: Call<GetPastFutureDateResponse>, t: Throwable) {
                Log.e(ScheduleModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
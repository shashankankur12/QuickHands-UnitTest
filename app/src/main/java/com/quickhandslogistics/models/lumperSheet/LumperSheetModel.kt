package com.quickhandslogistics.models.lumperSheet

import android.util.Log
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.data.lumperSheet.SubmitLumperSheetRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LumperSheetModel(private val sharedPref: SharedPref) : LumperSheetContract.Model {

    override fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: LumperSheetContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

        val date = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, selectedDate)
        val shiftDetail = ScheduleUtils.getShiftDetailString(leadProfile)
        val deptDetail = "${ ResourceManager.getInstance().getString(R.string.dept_bold)} ${ UIUtils.getDisplayEmployeeDepartment(leadProfile)}"
        onFinishedListener.onSuccessGetHeaderInfo(date, shiftDetail, deptDetail)
    }

    override fun fetchLumperSheetList(selectedDate: Date, onFinishedListener: LumperSheetContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getService().getLumperSheetList(getAuthToken(), dateString).enqueue(object : Callback<LumperSheetListAPIResponse> {
            override fun onResponse(call: Call<LumperSheetListAPIResponse>, response: Response<LumperSheetListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!, selectedDate)
                }
            }

            override fun onFailure(call: Call<LumperSheetListAPIResponse>, t: Throwable) {
                Log.e(LumperSheetModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun submitLumperSheet(selectedDate: Date, onFinishedListener: LumperSheetContract.Model.OnFinishedListener) {
        val request = SubmitLumperSheetRequest(DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate))

        DataManager.getService().submitLumperSheet(getAuthToken(), request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessSubmitLumperSheet()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(LumperSheetModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
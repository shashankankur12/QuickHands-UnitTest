package com.quickhandslogistics.models.workSheet

import android.util.Log
import com.quickhandslogistics.contracts.workSheet.WorkSheetContract
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkSheetModel(private val sharedPref: SharedPref) : WorkSheetContract.Model {

    override fun fetchHeaderInfo(onFinishedListener: WorkSheetContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

        var companyName = ""
        leadProfile?.buildingDetailData?.customerDetail?.companyName?.let { name ->
            companyName = name
        }
        val date = DateUtils.getCurrentDateStringByEmployeeShift(pattern = DateUtils.PATTERN_NORMAL)
        val dateShiftDetail = "$date - ${ScheduleUtils.getShiftDetailString(leadProfile)}"
        onFinishedListener.onSuccessGetHeaderInfo(companyName, dateShiftDetail)
    }

    override fun fetchWorkSheetList(onFinishedListener: WorkSheetContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getCurrentDateStringByEmployeeShift()

        DataManager.getService().getWorkSheetList(getAuthToken(), dateString).enqueue(object : Callback<WorkSheetListAPIResponse> {
            override fun onResponse(call: Call<WorkSheetListAPIResponse>, response: Response<WorkSheetListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessFetchWorkSheet(response.body()!!)
                }
            }

            override fun onFailure(call: Call<WorkSheetListAPIResponse>, t: Throwable) {
                Log.e(WorkSheetModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
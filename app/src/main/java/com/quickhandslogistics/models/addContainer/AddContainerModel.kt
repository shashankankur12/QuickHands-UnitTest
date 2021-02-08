package com.quickhandslogistics.models.addContainer

import android.util.Log
import com.quickhandslogistics.contracts.addContainer.AddContainerContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.addContainer.AddContainerRequest
import com.quickhandslogistics.data.addContainer.ContainerDetails
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.models.workSheet.AllWorkScheduleCancelModel
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.sharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddContainerModel: AddContainerContract.Model {

    override fun addTodayWorkContainer(uploadContainer: ArrayList<ContainerDetails>, liveLoadContainer: ArrayList<ContainerDetails>, dropOffContainer: ArrayList<ContainerDetails>, onFinishedListener: AddContainerContract.Model.OnFinishedListener) {
        val day = DateUtils.getCurrentDateStringByEmployeeShift()
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
        var shiftDetail = ""
        var buildingName = ""
        leadProfile?.buildingDetailData?.buildingName?.let { name ->
            buildingName = name
        }
        leadProfile?.shift?.let { name->
            shiftDetail=name
        }
        val request = AddContainerRequest(day,uploadContainer,liveLoadContainer,dropOffContainer,buildingName,shiftDetail)

        DataManager.getService().addSchedulesWorkItem(DataManager.getAuthToken(), request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (DataManager.isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessAddTodayWorkContainer()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(AllWorkScheduleCancelModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
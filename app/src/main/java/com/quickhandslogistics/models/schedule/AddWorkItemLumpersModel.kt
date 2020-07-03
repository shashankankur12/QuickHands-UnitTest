package com.quickhandslogistics.models.schedule

import android.util.Log
import com.quickhandslogistics.contracts.schedule.AddWorkItemLumpersContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.common.AllLumpersResponse
import com.quickhandslogistics.data.schedule.AssignLumpersRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AddWorkItemLumpersModel(private val sharedPref: SharedPref) : AddWorkItemLumpersContract.Model {

    override fun fetchLumpersList(onFinishedListener: AddWorkItemLumpersContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getCurrentDateStringByEmployeeShift(sharedPref)

        DataManager.getService().getPresentLumpersList(getAuthToken(), dateString).enqueue(object : Callback<AllLumpersResponse> {
            override fun onResponse(call: Call<AllLumpersResponse>, response: Response<AllLumpersResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessFetchLumpers(response.body()!!)
                }
            }

            override fun onFailure(call: Call<AllLumpersResponse>, t: Throwable) {
                Log.e(AddWorkItemLumpersModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun assignLumpersList(
        workItemId: String, workItemType: String, selectedLumperIdsList: ArrayList<String>,
        tempLumperIdsList: ArrayList<String>, onFinishedListener: AddWorkItemLumpersContract.Model.OnFinishedListener
    ) {
        val request = AssignLumpersRequest(sharedPref.getString(AppConstant.PREFERENCE_BUILDING_ID), workItemType, selectedLumperIdsList, tempLumperIdsList)

        DataManager.getService().assignLumpers(getAuthToken(), workItemId, request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessAssignLumpers()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(AddWorkItemLumpersModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
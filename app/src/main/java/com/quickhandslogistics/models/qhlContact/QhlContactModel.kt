package com.quickhandslogistics.models.qhlContact

import android.util.Log
import com.quickhandslogistics.contracts.qhlContact.QhlContactContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.qhlContact.QhlContactListResponse
import com.quickhandslogistics.data.qhlContact.QhlOfficeInfoResponse
import com.quickhandslogistics.models.lumpers.LumpersModel
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QhlContactModel(private val sharedPref: SharedPref) : QhlContactContract.Model{
    override fun fetchQhlHeaderInfo(onFinishedListener: QhlContactContract.Model.OnFinishedListener) {

        DataManager.getService().getQhlOfficeInfo(DataManager.getAuthToken()).enqueue(object : Callback<QhlOfficeInfoResponse> {
            override fun onResponse(call: Call<QhlOfficeInfoResponse>, response: Response<QhlOfficeInfoResponse>) {
                if (DataManager.isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessGetHeaderInfo(response.body())
                }
            }

            override fun onFailure(call: Call<QhlOfficeInfoResponse>, t: Throwable) {
                Log.e(LumpersModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun fetchQhlContactList(onFinishedListener: QhlContactContract.Model.OnFinishedListener) {

        DataManager.getService().getQhlContactList(DataManager.getAuthToken()).enqueue(object : Callback<QhlContactListResponse> {
            override fun onResponse(call: Call<QhlContactListResponse>, response: Response<QhlContactListResponse>) {
                if (DataManager.isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<QhlContactListResponse>, t: Throwable) {
                Log.e(LumpersModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

}
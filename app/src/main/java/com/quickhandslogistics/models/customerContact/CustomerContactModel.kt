package com.quickhandslogistics.models.customerContact

import android.util.Log
import com.quickhandslogistics.contracts.customerContact.CustomerContactContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.data.qhlContact.QhlContactListResponse
import com.quickhandslogistics.models.lumpers.LumpersModel
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerContactModel(private val sharedPref: SharedPref) : CustomerContactContract.Model{
    override fun fetchHeaderInfo(onFinishedListener: CustomerContactContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
        val buildingDetails= leadProfile?.buildingDetailData?.get(0)
        onFinishedListener.onSuccessGetHeaderInfo(buildingDetails)
    }

    override fun fetchCustomerContactList(onFinishedListener: CustomerContactContract.Model.OnFinishedListener) {

        DataManager.getService().getCustomerContactList(DataManager.getAuthToken()).enqueue(object : Callback<QhlContactListResponse> {
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
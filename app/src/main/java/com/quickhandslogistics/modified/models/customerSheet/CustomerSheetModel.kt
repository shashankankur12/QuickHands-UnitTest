package com.quickhandslogistics.modified.models.customerSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.customerSheet.CustomerSheetListAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.createMultiPartBody
import com.quickhandslogistics.network.DataManager.createRequestBody
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class CustomerSheetModel(private val sharedPref: SharedPref) : CustomerSheetContract.Model {

    override fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: CustomerSheetContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

        var buildingName = ""
        leadProfile?.buildingDetailData?.buildingName?.let { name ->
            buildingName = name
        }
        val date = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, selectedDate)
        onFinishedListener.onSuccessGetHeaderInfo(buildingName, date)
    }

    override fun fetchCustomerSheetList(selectedDate: Date, onFinishedListener: CustomerSheetContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getService().getCustomerSheetList(getAuthToken(), dateString).enqueue(object : Callback<CustomerSheetListAPIResponse> {
            override fun onResponse(call: Call<CustomerSheetListAPIResponse>, response: Response<CustomerSheetListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessFetchCustomerSheet(response.body()!!, selectedDate)
                }
            }

            override fun onFailure(call: Call<CustomerSheetListAPIResponse>, t: Throwable) {
                Log.e(CustomerSheetModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun saveCustomerSheet(
        customerName: String, notesCustomer: String, signatureFilePath: String, onFinishedListener: CustomerSheetContract.Model.OnFinishedListener
    ) {
        val nameRequestBody = createRequestBody(customerName)
        val notesRequestBody = createRequestBody(notesCustomer)

        val signatureFile = File(signatureFilePath)
        val signatureMultiPartBody = createMultiPartBody(signatureFile, "signature")

        DataManager.getService().saveCustomerSheet(getAuthToken(), nameRequestBody, notesRequestBody, signatureMultiPartBody)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                        // Delete the temporary saved file
                        signatureFile.delete()

                        onFinishedListener.onSuccessSaveCustomerSheet()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.e(CustomerSheetModel::class.simpleName, t.localizedMessage!!)
                    onFinishedListener.onFailure()
                }
            })
    }
}
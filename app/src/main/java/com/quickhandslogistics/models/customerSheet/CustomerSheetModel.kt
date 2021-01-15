package com.quickhandslogistics.models.customerSheet

import android.util.Log
import com.quickhandslogistics.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.customerSheet.CustomerSheetListAPIResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.createMultiPartBody
import com.quickhandslogistics.network.DataManager.createRequestBody
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.utils.SharedPref
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class CustomerSheetModel(private val sharedPref: SharedPref) : CustomerSheetContract.Model {

    override fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: CustomerSheetContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

        var companyName = ""
        leadProfile?.buildingDetailData?.customerDetail?.companyName?.let { name ->
            companyName = name
        }
        val date = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, selectedDate)
        val dateShiftDetail = "$date  ${ScheduleUtils.getShiftDetailString(leadProfile)}"
        onFinishedListener.onSuccessGetHeaderInfo(companyName, dateShiftDetail)
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

    override fun saveCustomerSheet(customerName: String, notesCustomer: String, signatureFilePath: String, customerId: String, onFinishedListener: CustomerSheetContract.Model.OnFinishedListener) {
        val nameRequestBody = createRequestBody(customerName)
        val notesRequestBody = createRequestBody(notesCustomer)
        val customerIdBody = createRequestBody(customerId)
        var signatureFile :File? =null
        var signatureMultiPartBody :MultipartBody.Part?=null
        if (signatureFilePath.isNotEmpty()) {
            signatureFile = File(signatureFilePath)
             signatureMultiPartBody = createMultiPartBody(signatureFile, "signature")
        }

        DataManager.getService().saveCustomerSheet(getAuthToken(), nameRequestBody, notesRequestBody, signatureMultiPartBody, customerIdBody)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                        // Delete the temporary saved file
                        if (!signatureFilePath.isNullOrEmpty())
                        signatureFile?.delete()

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
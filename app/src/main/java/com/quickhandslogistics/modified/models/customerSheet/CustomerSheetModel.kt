package com.quickhandslogistics.modified.models.customerSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class CustomerSheetModel(private val sharedPref: SharedPref) :
    CustomerSheetContract.Model {

    override fun fetchHeaderInfo(
        selectedDate: Date, onFinishedListener: CustomerSheetContract.Model.OnFinishedListener
    ) {
        val leadProfile = sharedPref.getClassObject(
            AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java
        ) as LeadProfileData?

        var buildingName = ""
        leadProfile?.buildingDetailData?.buildingName?.let { name ->
            buildingName = name
        }
        val date = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, selectedDate)
        onFinishedListener.onSuccessGetHeaderInfo(buildingName, date)
    }

    override fun fetchCustomerSheetList(
        selectedDate: Date, onFinishedListener: CustomerSheetContract.Model.OnFinishedListener
    ) {
        val dateString =
            DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getWorkSheetList(
            dateString, object : ResponseListener<WorkSheetListAPIResponse> {
                override fun onSuccess(response: WorkSheetListAPIResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccessFetchCustomerSheet(response)
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(CustomerSheetModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
package com.quickhandslogistics.modified.models.workSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class WorkSheetModel(private val sharedPref: SharedPref) :
    WorkSheetContract.Model {

    override fun fetchHeaderInfo(onFinishedListener: WorkSheetContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(
            AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java
        ) as LeadProfileData?

        var buildingName = ""
        leadProfile?.buildingDetailData?.buildingName?.let { name ->
            buildingName = name
        }
        val date = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, Date())
        onFinishedListener.onSuccessGetHeaderInfo(buildingName, date)
    }

    override fun fetchWorkSheetList(onFinishedListener: WorkSheetContract.Model.OnFinishedListener) {
        val day = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, Date())

        DataManager.getWorkSheetList(
            day, object : ResponseListener<WorkSheetListAPIResponse> {
                override fun onSuccess(response: WorkSheetListAPIResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccessFetchWorkSheet(response)
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(WorkSheetModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
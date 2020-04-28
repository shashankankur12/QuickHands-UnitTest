package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.data.schedule.UnScheduleListAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class UnScheduleModel(private val sharedPref: SharedPref) : UnScheduleContract.Model {

    override fun fetchUnSchedulesByDate(
        onFinishedListener: UnScheduleContract.Model.OnFinishedListener
    ) {
        val buildingId = sharedPref.getString(AppConstant.PREFERENCE_BUILDING_ID)

        DataManager.getUnSchedulesList(
            buildingId,
            object : ResponseListener<UnScheduleListAPIResponse> {
                override fun onSuccess(response: UnScheduleListAPIResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccess(response)
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(UnScheduleModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
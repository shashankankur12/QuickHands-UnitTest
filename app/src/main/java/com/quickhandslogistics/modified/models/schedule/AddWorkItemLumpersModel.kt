package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.AddWorkItemLumpersContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.AssignLumpersRequest
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import okhttp3.ResponseBody

class AddWorkItemLumpersModel(private val sharedPref: SharedPref) :
    AddWorkItemLumpersContract.Model {

    override fun fetchLumpersList(onFinishedListener: AddWorkItemLumpersContract.Model.OnFinishedListener) {
        DataManager.getAllLumpersData(object : ResponseListener<AllLumpersResponse> {
            override fun onSuccess(response: AllLumpersResponse) {
                if (response.success) {
                    onFinishedListener.onSuccessFetchLumpers(response)
                } else {
                    onFinishedListener.onFailure(response.message)
                }
            }

            override fun onError(error: Any) {
                if (error is Throwable) {
                    Log.e(AddWorkItemLumpersModel::class.simpleName, error.localizedMessage!!)
                    onFinishedListener.onFailure()
                } else if (error is String) {
                    onFinishedListener.onFailure(error)
                }
            }
        })
    }

    override fun assignLumpersList(
        workItemId: String,
        workItemType: String,
        employeeDataList: ArrayList<EmployeeData>,
        onFinishedListener: AddWorkItemLumpersContract.Model.OnFinishedListener
    ) {
        val lumperIds = ArrayList<String>()
        for (employee in employeeDataList) {
            lumperIds.add(employee.id!!)
        }
        val assignLumperRequest = AssignLumpersRequest(
            sharedPref.getString(AppConstant.PREFERENCE_BUILDING_ID),
            workItemType, lumperIds
        )
        DataManager.assignLumpers(
            workItemId,
            assignLumperRequest,
            object : ResponseListener<ResponseBody> {
                override fun onSuccess(response: ResponseBody) {
//                if (response.success) {
//                    onFinishedListener.onSuccessFetchLumpers(response)
//                } else {
//                    onFinishedListener.onFailure(response.message)
//                }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(AddWorkItemLumpersModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
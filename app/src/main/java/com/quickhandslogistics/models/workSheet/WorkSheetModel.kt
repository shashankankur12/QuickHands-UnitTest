package com.quickhandslogistics.models.workSheet

import android.util.Log
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.WorkSheetContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.workSheet.CancelAllSchedulesRequest
import com.quickhandslogistics.data.workSheet.SaveNoteWorkItemRequest
import com.quickhandslogistics.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkSheetModel(private val sharedPref: SharedPref) : WorkSheetContract.Model {

    override fun fetchHeaderInfo(onFinishedListener: WorkSheetContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

        var companyName = ""
        leadProfile?.buildingDetailData?.customerDetail?.name?.let { name ->
            companyName = name
        }
        val date = DateUtils.getCurrentDateStringByEmployeeShift(pattern = DateUtils.PATTERN_NORMAL)
        val shiftDetail = ScheduleUtils.getShiftDetailString(leadProfile)
        val deptDetail = "${ ResourceManager.getInstance().getString(R.string.dept_bold)} ${ UIUtils.getDisplayEmployeeDepartment(leadProfile)}"
        onFinishedListener.onSuccessGetHeaderInfo(companyName, date, shiftDetail,deptDetail)
    }

    override fun fetchWorkSheetList(onFinishedListener: WorkSheetContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getCurrentDateStringByEmployeeShift()

        DataManager.getService().getWorkSheetList(getAuthToken(), dateString).enqueue(object : Callback<WorkSheetListAPIResponse> {
            override fun onResponse(call: Call<WorkSheetListAPIResponse>, response: Response<WorkSheetListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessFetchWorkSheet(response.body()!!)
                }
            }

            override fun onFailure(call: Call<WorkSheetListAPIResponse>, t: Throwable) {
                Log.e(WorkSheetModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun saveGroupNoteData(onFinishedListener: WorkSheetContract.Model.OnFinishedListener, containerIds: ArrayList<String>, containerType: String, customerNote: String, qhlNote: String) {
        val dateString = DateUtils.getCurrentDateStringByEmployeeShift()
        val request = SaveNoteWorkItemRequest( containerIds, containerType, customerNote, qhlNote)
        DataManager.getService().saveGroupNoteSchedules(getAuthToken(), dateString, request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessSaveGroupNoteWorkSheet(response.body()!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(WorkSheetModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
    override fun removeNote(onFinishedListener: WorkSheetContract.Model.OnFinishedListener, id: String) {
        DataManager.getService().saveGroupNoteSchedules(getAuthToken(), id).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessRemoveNote(response.body()!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(WorkSheetModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

}
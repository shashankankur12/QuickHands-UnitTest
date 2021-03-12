package com.quickhandslogistics.models.schedule

import android.util.Log
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.schedule.WorkScheduleContract
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.schedule.ScheduleDetailAPIResponse
import com.quickhandslogistics.data.schedule.ScheduleListAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class WorkScheduleModel (private val sharedPref: SharedPref) : WorkScheduleContract.Model {

    override fun fetchHeaderInfo(date: Date, onFinishedListener: WorkScheduleContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

        var companyName = ""
        leadProfile?.buildingDetailData?.customerDetail?.name?.let { name ->
            companyName = name
        }
        val date = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, date)
        val shiftDetail = ScheduleUtils.getShiftDetailString(leadProfile)
        val deptDetail = "${ ResourceManager.getInstance().getString(R.string.dept_bold)} ${ UIUtils.getDisplayEmployeeDepartment(leadProfile)}"
        onFinishedListener.onSuccessGetHeaderInfo(companyName, date, shiftDetail,deptDetail)
    }

    override fun fetchScheduleDetail(scheduleIdentityId: String, selectedDate: Date, onFinishedListener: WorkScheduleContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
        val deptDetail =  UIUtils.getDisplayEmployeeDepartment(leadProfile).toUpperCase()

        DataManager.getService().getSchedulesDetails(DataManager.getAuthToken(), dateString, deptDetail, 1, AppConstant.API_PAGE_SIZE)
            .enqueue(object : Callback<ScheduleListAPIResponse> {
                override fun onResponse(call: Call<ScheduleListAPIResponse>, response: Response<ScheduleListAPIResponse>) {
                    if (DataManager.isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                        onFinishedListener.onSuccessFetchWorkSheet(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<ScheduleListAPIResponse>, t: Throwable) {
                    Log.e(ScheduleDetailModel::class.simpleName, t.localizedMessage!!)
                    onFinishedListener.onFailure()
                }
            })
    }
}
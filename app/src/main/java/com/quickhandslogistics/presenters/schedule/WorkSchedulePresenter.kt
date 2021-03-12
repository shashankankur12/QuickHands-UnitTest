package com.quickhandslogistics.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.schedule.WorkScheduleContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.schedule.ScheduleDetailData
import com.quickhandslogistics.data.schedule.ScheduleListAPIResponse
import com.quickhandslogistics.models.schedule.WorkScheduleModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class WorkSchedulePresenter (private var workSheetView: WorkScheduleContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    WorkScheduleContract.Presenter, WorkScheduleContract.Model.OnFinishedListener  {

    private val workSheetModel = WorkScheduleModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        workSheetView = null
    }

    override fun fetchWorkSheetList(scheduleDepartment: String, selectedDate: Date) {
        workSheetView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        workSheetModel.fetchHeaderInfo(selectedDate,this)
        workSheetModel.fetchScheduleDetail(scheduleDepartment, selectedDate, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        workSheetView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            workSheetView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            workSheetView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        workSheetView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            workSheetView?.showLoginScreen()
        }
    }

    override fun onSuccessFetchWorkSheet(scheduleDetailAPIResponse: ScheduleListAPIResponse) {
        workSheetView?.hideProgressDialog()
//        workSheetListAPIResponse.data?.let { data ->
//
//            // Sort all the work Items by their Start Time
//            data.inProgress?.sortWith(Comparator { workItem1, workItem2 ->
//                workItem1.startTime!!.compareTo(workItem2.startTime!!)
//            })
//            data.onHold?.sortWith(Comparator { workItem1, workItem2 ->
//                workItem1.startTime!!.compareTo(workItem2.startTime!!)
//            })
//            data.scheduled?.sortWith(Comparator { workItem1, workItem2 ->
//                workItem1.startTime!!.compareTo(workItem2.startTime!!)
//            })
//            data.cancelled?.sortWith(Comparator { workItem1, workItem2 ->
//                workItem1.startTime!!.compareTo(workItem2.startTime!!)
//            })
//            data.completed?.sortWith(Comparator { workItem1, workItem2 ->
//                workItem1.startTime!!.compareTo(workItem2.startTime!!)
//            })
//
//
        var data = ScheduleDetailData()
        scheduleDetailAPIResponse.data!!.scheduleDetailsList?.let { it1 ->
            it1.forEach { scheduleDetail ->
                if (!scheduleDetail.drops.isNullOrEmpty() || !scheduleDetail.liveLoads.isNullOrEmpty() || !scheduleDetail.outbounds.isNullOrEmpty())
                    data = scheduleDetail

            }
        }

        workSheetView?.showWorkSheets(data)

//        }
    }

    override fun onSuccessGetHeaderInfo(companyName: String, date: String, shift: String, dept: String) {
        workSheetView?.showHeaderInfo(companyName, date, shift, dept)
    }
}
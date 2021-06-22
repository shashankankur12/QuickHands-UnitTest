package com.quickhandslogistics.presenters.lumperSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.data.schedule.GetPastFutureDateResponse
import com.quickhandslogistics.models.lumperSheet.LumperSheetModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class LumperSheetPresenter(private var lumperSheetView: LumperSheetContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    LumperSheetContract.Presenter, LumperSheetContract.Model.OnFinishedListener {

    private val lumperSheetModel = LumperSheetModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        lumperSheetView = null
    }

    override fun getLumpersSheetByDate(selectedDate: Date) {
        lumperSheetView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        lumperSheetModel.fetchPastFutureDate(this)
        lumperSheetModel.fetchHeaderInfo(selectedDate, this)
        lumperSheetModel.fetchLumperSheetList(selectedDate, this)
    }

    override fun initiateSheetSubmission(selectedDate: Date) {
        lumperSheetView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        lumperSheetModel.submitLumperSheet(selectedDate, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        lumperSheetView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumperSheetView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            lumperSheetView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        lumperSheetView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            lumperSheetView?.showLoginScreen()
        }
    }

    override fun onSuccess(response: LumperSheetListAPIResponse, selectedDate: Date) {
        lumperSheetView?.hideProgressDialog()

        response.data?.let { data ->
            var lumperInfoList: ArrayList<LumpersInfo> = ArrayList()
            var tempLumperIds: ArrayList<String> =ArrayList()
            lumperInfoList.addAll(data.lumpersInfo!!)
            lumperInfoList.addAll(data.tempLumperIds!!)

            data.tempLumperIds?.forEach {
                tempLumperIds.add(it.lumperId!!)
            }
            lumperSheetView?.showLumperSheetData(lumperInfoList, data.isSheetSubmitted!!, selectedDate, tempLumperIds)
        }

    }

    override fun onSuccessSubmitLumperSheet() {
        lumperSheetView?.hideProgressDialog()
        lumperSheetView?.sheetSubmittedSuccessfully()
    }

    override fun onSuccessGetHeaderInfo(dateString: String, shift: String, dept: String) {
        lumperSheetView?.showDateString(dateString, shift, dept)
    }

    override fun onSuccessPastFutureDate(response: GetPastFutureDateResponse?) {
        response?.data?.let {
            lumperSheetView?.showPastFutureDate(it)
        }
    }
}
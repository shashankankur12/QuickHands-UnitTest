package com.quickhandslogistics.presenters.lumperSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.models.lumperSheet.LumperSheetModel
import com.quickhandslogistics.utils.DateUtils
import java.util.*

class LumperSheetPresenter(private var lumperSheetView: LumperSheetContract.View?, private val resources: Resources) :
    LumperSheetContract.Presenter, LumperSheetContract.Model.OnFinishedListener {

    private val lumperSheetModel = LumperSheetModel()

    /** View Listeners */
    override fun onDestroy() {
        lumperSheetView = null
    }

    override fun getLumpersSheetByDate(selectedDate: Date) {
        lumperSheetView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
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

    override fun onSuccess(response: LumperSheetListAPIResponse, selectedDate: Date) {
        lumperSheetView?.hideProgressDialog()

        val dateString = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, selectedDate)
        lumperSheetView?.showDateString(dateString)

        response.data?.let { data ->
            lumperSheetView?.showLumperSheetData(data.lumpersInfo!!, data.isSheetSubmitted!!, selectedDate, data.tempLumperIds!!)
        }

    }

    override fun onSuccessSubmitLumperSheet() {
        lumperSheetView?.hideProgressDialog()
        lumperSheetView?.sheetSubmittedSuccessfully()
    }
}
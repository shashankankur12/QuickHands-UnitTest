package com.quickhandslogistics.modified.presenters.lumperSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.modified.models.lumperSheet.LumperSheetModel
import com.quickhandslogistics.utils.DateUtils
import java.util.*

class LumperSheetPresenter(
    private var lumperSheetView: LumperSheetContract.View?,
    private val resources: Resources
) : LumperSheetContract.Presenter, LumperSheetContract.Model.OnFinishedListener {

    private val lumperSheetModel: LumperSheetModel = LumperSheetModel()

    override fun getLumpersSheetByDate(selectedDate: Date) {
        lumperSheetView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        lumperSheetModel.fetchLumperSheetList(selectedDate, this)
    }

    override fun initiateSheetSubmission(selectedDate: Date) {
        lumperSheetView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        lumperSheetModel.submitLumperSheet(selectedDate, this)
    }

    override fun onDestroy() {
        lumperSheetView = null
    }

    override fun onFailure(message: String) {
        lumperSheetView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumperSheetView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            lumperSheetView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(response: LumperSheetListAPIResponse, selectedDate: Date) {
        lumperSheetView?.hideProgressDialog()

        val dateString = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, selectedDate)
        lumperSheetView?.showDateString(dateString)
        lumperSheetView?.showLumperSheetData(response.data?.lumpersInfo!!, response.data?.isSheetSubmitted!!, selectedDate)
    }

    override fun onSuccessSubmitLumperSheet() {
        lumperSheetView?.hideProgressDialog()
        lumperSheetView?.sheetSubmittedSuccessfully()
    }
}
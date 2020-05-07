package com.quickhandslogistics.modified.presenters.lumperSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperWorkDetailContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.models.lumperSheet.LumperWorkDetailModel
import java.util.*

class LumperWorkDetailPresenter(
    private var lumperWorkDetailView: LumperWorkDetailContract.View?,
    private val resources: Resources
) : LumperWorkDetailContract.Presenter, LumperWorkDetailContract.Model.OnFinishedListener {

    private val lumperWorkDetailModel: LumperWorkDetailModel = LumperWorkDetailModel()

    override fun getLumperWorkDetails(lumperId: String?, selectedDate: Date) {
        lumperWorkDetailView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        lumperWorkDetailModel.fetchLumperWorkDetails(lumperId, selectedDate, this)
    }

    override fun saveLumperSignature(lumperId: String, date: Date, signatureFilePath: String) {
        lumperWorkDetailView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        lumperWorkDetailModel.saveLumperSignature(lumperId, date, signatureFilePath, this)
    }

    override fun onDestroy() {
        lumperWorkDetailView = null
    }

    override fun onFailure(message: String) {
        lumperWorkDetailView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumperWorkDetailView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            lumperWorkDetailView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(response: AllLumpersResponse) {
        lumperWorkDetailView?.hideProgressDialog()
        lumperWorkDetailView?.showLumperWorkDetails(response.data!!)
    }

    override fun onSuccessSaveLumperSignature(lumperId: String, date: Date) {
        lumperWorkDetailModel.fetchLumperWorkDetails(lumperId, date, this)
    }
}
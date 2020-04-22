package com.quickhandslogistics.modified.presenters.lumpers

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.lumpers.LumpersContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.models.lumpers.LumpersModel
import com.quickhandslogistics.utils.StringUtils

class LumpersPresenter(
    private var lumpersView: LumpersContract.View?,
    private val resources: Resources
) : LumpersContract.Presenter, LumpersContract.Model.OnFinishedListener {

    private val lumpersModel: LumpersModel = LumpersModel()

    override fun fetchLumpersList() {
        lumpersView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        lumpersModel.fetchLumpersList(this)
    }

    override fun onDestroy() {
        lumpersView = null
    }

    override fun onFailure(message: String) {
        lumpersView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumpersView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            lumpersView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(allLumpersResponse: AllLumpersResponse) {
        lumpersView?.hideProgressDialog()
        lumpersView?.showLumpersData(allLumpersResponse.data!!)
    }
}
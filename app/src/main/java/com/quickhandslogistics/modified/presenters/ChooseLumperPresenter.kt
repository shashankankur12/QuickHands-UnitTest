package com.quickhandslogistics.modified.presenters

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.ChooseLumperContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.models.ChooseLumperModel
import com.quickhandslogistics.utils.StringUtils

class ChooseLumperPresenter(
    private var chooseLumperView: ChooseLumperContract.View?,
    private val resources: Resources
) : ChooseLumperContract.Presenter, ChooseLumperContract.Model.OnFinishedListener {

    private val chooseLumperModel: ChooseLumperModel = ChooseLumperModel()

    override fun fetchLumpersList() {
        chooseLumperView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        chooseLumperModel.fetchLumpersList(this)
    }

    override fun onDestroy() {
        chooseLumperView = null
    }

    override fun onFailure(message: String) {
        chooseLumperView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            chooseLumperView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            chooseLumperView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(allLumpersResponse: AllLumpersResponse) {
        chooseLumperView?.hideProgressDialog()
        chooseLumperView?.showLumpersData(allLumpersResponse.data!!)
    }
}
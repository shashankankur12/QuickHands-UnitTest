package com.quickhandslogistics.presenters.reports

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.reports.LumperJobHistoryContract
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.models.reports.LumperJobHistoryModel

class LumperJobHistoryPresenter(private var lumperJobHistoryView: LumperJobHistoryContract.View?, private val resources: Resources) :
    LumperJobHistoryContract.Presenter, LumperJobHistoryContract.Model.OnFinishedListener {

    private val lumperJobHistoryModel = LumperJobHistoryModel()

    /** View Listeners */
    override fun onDestroy() {
        lumperJobHistoryView = null
    }

    override fun fetchLumpersList() {
        lumperJobHistoryView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        lumperJobHistoryModel.fetchLumpersList(this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        lumperJobHistoryView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumperJobHistoryView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            lumperJobHistoryView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(response: LumperListAPIResponse) {
        lumperJobHistoryView?.hideProgressDialog()
        lumperJobHistoryView?.showLumpersData(response.data?.permanentLumpersList!!)
    }
}
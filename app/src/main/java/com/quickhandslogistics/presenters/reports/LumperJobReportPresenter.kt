package com.quickhandslogistics.presenters.reports

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.reports.LumperJobReportContract
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.models.reports.LumperJobReportModel

class LumperJobReportPresenter(private var lumperJobReportView: LumperJobReportContract.View?, private val resources: Resources) :
    LumperJobReportContract.Presenter, LumperJobReportContract.Model.OnFinishedListener {

    private val lumperJobHistoryModel = LumperJobReportModel()

    /** View Listeners */
    override fun onDestroy() {
        lumperJobReportView = null
    }

    override fun fetchLumpersList() {
        lumperJobReportView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        lumperJobHistoryModel.fetchLumpersList(this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        lumperJobReportView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumperJobReportView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            lumperJobReportView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(response: LumperListAPIResponse) {
        lumperJobReportView?.hideProgressDialog()
        lumperJobReportView?.showLumpersData(response.data?.permanentLumpersList!!)
    }
}
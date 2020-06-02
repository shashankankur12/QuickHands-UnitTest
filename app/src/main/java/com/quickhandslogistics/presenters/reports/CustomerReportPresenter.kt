package com.quickhandslogistics.presenters.reports

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.reports.CustomerReportContract
import com.quickhandslogistics.models.reports.CustomerReportModel

class CustomerReportPresenter(private var customerReportView: CustomerReportContract.View?, private val resources: Resources) :
    CustomerReportContract.Presenter, CustomerReportContract.Model.OnFinishedListener {

    private val customerReportModel = CustomerReportModel()

    /** View Listeners */
    override fun onDestroy() {
        customerReportView = null
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        customerReportView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            customerReportView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            customerReportView?.showAPIErrorMessage(message)
        }
    }
}
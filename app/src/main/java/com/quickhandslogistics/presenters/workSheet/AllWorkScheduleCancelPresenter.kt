package com.quickhandslogistics.presenters.workSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.AllWorkScheduleCancelContract
import com.quickhandslogistics.data.common.AllLumpersResponse
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.models.workSheet.AllWorkScheduleCancelModel
import com.quickhandslogistics.utils.SharedPref

class AllWorkScheduleCancelPresenter(
    private var allWorkScheduleCancelView: AllWorkScheduleCancelContract.View?, private val resources: Resources, sharedPref: SharedPref
) : AllWorkScheduleCancelContract.Presenter, AllWorkScheduleCancelContract.Model.OnFinishedListener {

    private val allWorkScheduleCancelModel = AllWorkScheduleCancelModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        allWorkScheduleCancelView = null
    }

    override fun fetchLumpersList() {
        allWorkScheduleCancelView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        allWorkScheduleCancelModel.fetchLumpersList(this)
    }

    override fun initiateCancellingWorkSchedules(selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String) {
        allWorkScheduleCancelView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        allWorkScheduleCancelModel.cancelAllWorkSchedules(selectedLumperIdsList, notesQHL, notesCustomer, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        allWorkScheduleCancelView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            allWorkScheduleCancelView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            allWorkScheduleCancelView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessFetchLumpers(response: AllLumpersResponse) {
        allWorkScheduleCancelView?.hideProgressDialog()

        val allLumpersList = ArrayList<EmployeeData>()
        allLumpersList.addAll(response.data?.permanentLumpersList!!)
        allLumpersList.addAll(response.data?.temporaryLumpers!!)

        allWorkScheduleCancelView?.showLumpersData(allLumpersList)
    }

    override fun onSuccessCancelWorkSchedules() {
        allWorkScheduleCancelView?.hideProgressDialog()
        allWorkScheduleCancelView?.cancellingWorkScheduleFinished()
    }
}
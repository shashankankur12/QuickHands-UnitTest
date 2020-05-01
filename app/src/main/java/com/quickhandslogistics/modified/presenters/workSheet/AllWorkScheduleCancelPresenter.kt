package com.quickhandslogistics.modified.presenters.workSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.workSheet.AllWorkScheduleCancelContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.models.workSheet.AllWorkScheduleCancelModel
import com.quickhandslogistics.utils.SharedPref

class AllWorkScheduleCancelPresenter(
    private var allWorkScheduleCancelView: AllWorkScheduleCancelContract.View?,
    private val resources: Resources, sharedPref: SharedPref
) : AllWorkScheduleCancelContract.Presenter,
    AllWorkScheduleCancelContract.Model.OnFinishedListener {

    private val allWorkScheduleCancelModel = AllWorkScheduleCancelModel(sharedPref)

    override fun fetchLumpersList() {
        allWorkScheduleCancelView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        allWorkScheduleCancelModel.fetchLumpersList(this)
    }

    override fun onDestroy() {
        allWorkScheduleCancelView = null
    }

    override fun initiateCancellingWorkSchedules(
        selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String
    ) {
        allWorkScheduleCancelView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        allWorkScheduleCancelModel.cancelAllWorkSchedules(
            selectedLumperIdsList, notesQHL, notesCustomer, this
        )
    }

    override fun onFailure(message: String) {
        allWorkScheduleCancelView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            allWorkScheduleCancelView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            allWorkScheduleCancelView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessFetchLumpers(allLumpersResponse: AllLumpersResponse) {
        allWorkScheduleCancelView?.hideProgressDialog()
        allWorkScheduleCancelView?.showLumpersData(allLumpersResponse.data!!)
    }

    override fun onSuccessCancelWorkSchedules() {
        allWorkScheduleCancelView?.hideProgressDialog()
        allWorkScheduleCancelView?.cancellingWorkScheduleFinished()
    }
}
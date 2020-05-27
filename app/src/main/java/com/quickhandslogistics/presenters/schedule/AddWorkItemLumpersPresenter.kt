package com.quickhandslogistics.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.schedule.AddWorkItemLumpersContract
import com.quickhandslogistics.data.common.AllLumpersResponse
import com.quickhandslogistics.models.schedule.AddWorkItemLumpersModel
import com.quickhandslogistics.utils.SharedPref

class AddWorkItemLumpersPresenter(
    private var addWorkItemLumpersView: AddWorkItemLumpersContract.View?, private val resources: Resources, sharedPref: SharedPref
) : AddWorkItemLumpersContract.Presenter, AddWorkItemLumpersContract.Model.OnFinishedListener {

    private val addWorkItemLumpersModel = AddWorkItemLumpersModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        addWorkItemLumpersView = null
    }

    override fun fetchLumpersList() {
        addWorkItemLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        addWorkItemLumpersModel.fetchLumpersList(this)
    }

    override fun initiateAssigningLumpers(selectedLumperIdsList: ArrayList<String>, tempLumperIdsList: ArrayList<String>, workItemId: String, workItemType: String) {
        addWorkItemLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        addWorkItemLumpersModel.assignLumpersList(workItemId, workItemType, selectedLumperIdsList, tempLumperIdsList, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        addWorkItemLumpersView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            addWorkItemLumpersView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            addWorkItemLumpersView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessFetchLumpers(response: AllLumpersResponse) {
        addWorkItemLumpersView?.hideProgressDialog()
        addWorkItemLumpersView?.showLumpersData(response.data?.permanentLumpersList!!, response.data?.temporaryLumpers!!)
    }

    override fun onSuccessAssignLumpers() {
        addWorkItemLumpersView?.hideProgressDialog()
        addWorkItemLumpersView?.lumperAssignmentFinished()
    }
}
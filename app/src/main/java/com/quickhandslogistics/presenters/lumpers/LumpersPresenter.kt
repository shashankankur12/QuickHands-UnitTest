package com.quickhandslogistics.presenters.lumpers

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.lumpers.LumpersContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.models.lumpers.LumpersModel
import com.quickhandslogistics.utils.SharedPref

class LumpersPresenter(private var lumpersView: LumpersContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    LumpersContract.Presenter, LumpersContract.Model.OnFinishedListener {

    private val lumpersModel = LumpersModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        lumpersView = null
    }

    override fun fetchLumpersList() {
        lumpersView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        lumpersModel.fetchHeaderInfo(this)
        lumpersModel.fetchLumpersList(this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        lumpersView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumpersView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            lumpersView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(response: LumperListAPIResponse) {
        lumpersView?.hideProgressDialog()

        val allLumpersList = ArrayList<EmployeeData>()
        allLumpersList.addAll(response.data?.permanentLumpersList!!)
        allLumpersList.addAll(response.data?.temporaryLumpers!!)

        lumpersView?.showLumpersData(allLumpersList)
    }

    override fun onSuccessGetHeaderInfo(dateString: String) {
        lumpersView?.showDateString(dateString)
    }
}
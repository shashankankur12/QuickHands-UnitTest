package com.quickhandslogistics.modified.presenters.lumpers

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.lumpers.LumpersContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.modified.models.lumpers.LumpersModel

class LumpersPresenter(private var lumpersView: LumpersContract.View?, private val resources: Resources) :
    LumpersContract.Presenter, LumpersContract.Model.OnFinishedListener {

    private val lumpersModel = LumpersModel()

    /** View Listeners */
    override fun onDestroy() {
        lumpersView = null
    }

    override fun fetchLumpersList() {
        lumpersView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        lumpersModel.fetchLumpersList(this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        lumpersView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumpersView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
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
}
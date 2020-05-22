package com.quickhandslogistics.modified.presenters.common

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.common.ChooseLumpersContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.modified.models.common.ChooseLumpersModel

class ChooseLumpersPresenter(private var chooseLumpersView: ChooseLumpersContract.View?, private val resources: Resources) :
    ChooseLumpersContract.Presenter, ChooseLumpersContract.Model.OnFinishedListener {

    private val chooseLumpersModel = ChooseLumpersModel()

    /** View Listeners */
    override fun onDestroy() {
        chooseLumpersView = null
    }

    override fun fetchLumpersList() {
        chooseLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        chooseLumpersModel.fetchLumpersList(this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        chooseLumpersView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            chooseLumpersView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            chooseLumpersView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(response: LumperListAPIResponse) {
        chooseLumpersView?.hideProgressDialog()

        val allLumpersList = ArrayList<EmployeeData>()
        allLumpersList.addAll(response.data?.permanentLumpersList!!)
        allLumpersList.addAll(response.data?.temporaryLumpers!!)

        chooseLumpersView?.showLumpersData(allLumpersList)
    }
}
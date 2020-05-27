package com.quickhandslogistics.presenters.common

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.common.ChooseLumpersContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.models.common.ChooseLumpersModel
import java.util.*
import kotlin.collections.ArrayList

class ChooseLumpersPresenter(private var chooseLumpersView: ChooseLumpersContract.View?, private val resources: Resources) :
    ChooseLumpersContract.Presenter, ChooseLumpersContract.Model.OnFinishedListener {

    private val chooseLumpersModel = ChooseLumpersModel()

    /** View Listeners */
    override fun onDestroy() {
        chooseLumpersView = null
    }

    override fun fetchLumpersList(selectedDate: Date) {
        chooseLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        chooseLumpersModel.fetchLumpersList(selectedDate, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        chooseLumpersView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            chooseLumpersView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
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
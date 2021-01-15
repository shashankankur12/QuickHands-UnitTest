package com.quickhandslogistics.presenters.scheduleTime

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.scheduleTime.ChooseLumpersContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.models.scheduleTime.ChooseLumpersModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
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

    override fun onErrorCode(errorCode: ErrorResponse) {
        chooseLumpersView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            chooseLumpersView?.showLoginScreen()
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
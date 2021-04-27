package com.quickhandslogistics.presenters.addContainer

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.addContainer.AddContainerContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.addContainer.ContainerDetails
import com.quickhandslogistics.models.addContainer.AddContainerModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class AddContainerPresenter(private var addContainerContractView: AddContainerContract.View?, private val resources: Resources): AddContainerContract.Presenter, AddContainerContract.Model.OnFinishedListener {

    private val addContainerModel = AddContainerModel()
    
    /** View Listeners */
    override fun addTodayWorkContainer(
        uploadContainer: ArrayList<ContainerDetails>,
        liveLoadContainer: ArrayList<ContainerDetails>,
        dropOffContainer: ArrayList<ContainerDetails>,
        scheduleNote: String
    ) {
        addContainerContractView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        addContainerModel.addTodayWorkContainer(scheduleNote,uploadContainer, liveLoadContainer, dropOffContainer, this)
    }

    override fun onDestroy() {
        addContainerContractView=null
    }

    /** Model Result Listeners */
    override fun onSuccessAddTodayWorkContainer(response: BaseResponse?) {
        addContainerContractView?.hideProgressDialog()
        response?.let {
            addContainerContractView?.addWorkScheduleFinished(it.message)
        }

    }

    override fun onFailure(message: String) {
        addContainerContractView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            addContainerContractView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            addContainerContractView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        addContainerContractView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            addContainerContractView?.showLoginScreen()
        }
    }
}
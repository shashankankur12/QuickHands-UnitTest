package com.quickhandslogistics.presenters.lumpers

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.lumpers.LumperDetailsContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.lumpers.BuildingDetailsResponse
import com.quickhandslogistics.models.lumpers.LumperDetailsModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class LumpersDetailsPresenter(private var lumpersDetailsView: LumperDetailsContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    LumperDetailsContract.Presenter, LumperDetailsContract.Model.OnFinishedListener {

    private val lumperDetailsModel = LumperDetailsModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        lumpersDetailsView = null
    }

    override fun fetchBuildingInfo(originalBuildingId: String?) {
        lumpersDetailsView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        lumperDetailsModel.fetchBuildingInfo(originalBuildingId, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        lumpersDetailsView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumpersDetailsView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            lumpersDetailsView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        lumpersDetailsView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            lumpersDetailsView?.showLoginScreen()
        }
    }

    override fun onSuccess(response: BuildingDetailsResponse?) {
        lumpersDetailsView?.hideProgressDialog()
        response?.data?.let {
            lumpersDetailsView?.showBuildingInfo(it)
        }
    }

}
package com.quickhandslogistics.modified.presenters

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.ForgotPasswordContract
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordResponse
import com.quickhandslogistics.modified.models.ForgotPasswordModel

class ForgotPasswordPresenter(private var passwordView: ForgotPasswordContract.View?, private val resources: Resources) :
    ForgotPasswordContract.Presenter, ForgotPasswordContract.Model.OnFinishedListener {

    private val forgotPasswordModel = ForgotPasswordModel()

    /** View Listeners */
    override fun onDestroy() {
        passwordView = null
    }

    override fun validatePasswordResetDetails(employeeId: String) {
        forgotPasswordModel.validatePasswordResetDetails(employeeId, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        passwordView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            passwordView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            passwordView?.showAPIErrorMessage(message)
        }
    }

    override fun emptyEmployeeId() {
        passwordView?.showEmptyEmployeeIdError()
    }

    override fun onPasswordResetSuccess(forgotPasswordResponse: ForgotPasswordResponse) {
        passwordView?.hideProgressDialog()
        passwordView?.showAPISuccessMessage(forgotPasswordResponse.message)
    }

    override fun processPasswordReset(employeeId: String) {
        passwordView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        forgotPasswordModel.resetPasswordUsingEmpId(employeeId, this)
    }
}
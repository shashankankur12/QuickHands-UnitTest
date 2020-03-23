package com.quickhandslogistics.modified.presenters

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.ForgotPasswordContract
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordResponse
import com.quickhandslogistics.modified.models.ForgotPasswordModel

class ForgotPasswordPresenter(
    private var passwordView: ForgotPasswordContract.View?,
    private val resources: Resources
) : ForgotPasswordContract.Presenter, ForgotPasswordContract.Model.OnFinishedListener {

    private val forgotPasswordModel: ForgotPasswordModel = ForgotPasswordModel()

    override fun onDestroy() {
        passwordView = null
    }

    override fun validatePasswordResetDetails(employeeId: String) {
        forgotPasswordModel.validatePasswordResetDetails(employeeId, this)
    }

    override fun emptyEmployeeId() {
        passwordView?.showEmptyEmployeeIdError()
    }

    override fun onPasswordResetSuccess(forgotPasswordResponse: ForgotPasswordResponse) {
        passwordView?.hideProgress()
        passwordView?.showAPISuccessMessage(forgotPasswordResponse.message)
    }

    override fun onFailure(message: String) {
        passwordView?.hideProgress()
        if (TextUtils.isEmpty(message)) {
            passwordView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            passwordView?.showAPIErrorMessage(message)
        }
    }

    override fun processPasswordReset(employeeId: String) {
        passwordView?.showProgress(resources.getString(R.string.please_wait))
        forgotPasswordModel.resetPasswordUsingEmpId(employeeId, this)
    }
}
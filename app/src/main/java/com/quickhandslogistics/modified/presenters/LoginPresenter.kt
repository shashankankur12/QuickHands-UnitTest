package com.quickhandslogistics.modified.presenters

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LoginContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.modified.models.LoginModel
import com.quickhandslogistics.utils.SharedPref

class LoginPresenter(private var loginView: LoginContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    LoginContract.Presenter, LoginContract.Model.OnFinishedListener {

    private val loginModel = LoginModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        loginView = null
    }

    override fun loadEmployeeId() {
        loginModel.fetchEmployeeId(this)
    }

    override fun validateLoginDetails(employeeLoginId: String, password: String) {
        loginModel.validateLoginDetails(employeeLoginId, password, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        loginView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            loginView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            loginView?.showAPIErrorMessage(message)
        }
    }

    override fun emptyEmployeeId() {
        loginView?.showEmptyEmployeeIdError()
    }

    override fun emptyPassword() {
        loginView?.showEmptyPasswordError()
    }

    override fun invalidPassword() {
        loginView?.showInvalidPasswordError()
    }

    override fun onLeadProfileSuccess(leadProfileAPIResponse: LeadProfileAPIResponse) {
        loginView?.hideProgressDialog()
        leadProfileAPIResponse.data?.let {
            loginModel.processLeadProfileData(it, this)
        }
    }

    override fun onLoadEmployeeId(employeeId: String) {
        loginView?.loadEmployeeId(employeeId)
    }

    override fun onLoginSuccess(loginResponse: LoginResponse) {
        loginModel.processLoginData(loginResponse.data)
        loginModel.fetchLeadProfileInfo(this)
    }

    override fun onRegistrationTakenSaved(employeeLoginId: String, password: String) {
        loginModel.loginUsingEmployeeDetails(employeeLoginId, password, this)
    }

    override fun processCredentials(employeeLoginId: String, password: String) {
        loginView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        loginModel.fetchRegistrationToken(employeeLoginId, password, this)
    }

    override fun showNextScreen() {
        loginView?.showNextScreen()
    }
}
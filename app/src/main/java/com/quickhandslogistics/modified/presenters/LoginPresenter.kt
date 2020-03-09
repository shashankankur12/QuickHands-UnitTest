package com.quickhandslogistics.modified.presenters

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.modified.contracts.LoginContract
import com.quickhandslogistics.modified.models.LoginModel
import com.quickhandslogistics.utils.SharedPref

class LoginPresenter(
    private var loginView: LoginContract.View?,
    private val resources: Resources,
    sharedPref: SharedPref
) : LoginContract.Presenter, LoginContract.Model.OnFinishedListener {

    private val loginModel: LoginModel = LoginModel(sharedPref)

    override fun loadEmployeeId() {
        loginModel.fetchEmployeeId(this)
    }

    override fun onDestroy() {
        loginView = null
    }

    override fun validateLoginDetails(employeeLoginId: String, password: String) {
        loginModel.validateLoginDetails(employeeLoginId, password, this)
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

    override fun onLoadEmployeeId(employeeId: String) {
        loginView?.loadEmployeeId(employeeId)
    }

    override fun processCredentials(employeeLoginId: String, password: String) {
        loginView?.showProgress(resources.getString(R.string.logging_in))
        loginModel.loginUsingEmployeeDetails(employeeLoginId, password, this)
    }

    override fun onLoginSuccess(loginResponse: LoginResponse) {
        loginView?.hideProgress()
        loginModel.processLoginData(loginResponse.data, this)
    }

    override fun onFailure(message: String) {
        loginView?.hideProgress()
        if (TextUtils.isEmpty(message)) {
            loginView?.showAPIErrorMessage(resources.getString(R.string.invalid_email_password))
        } else {
            loginView?.showAPIErrorMessage(message)
        }
    }

    override fun showNextScreen() {
        loginView?.showNextScreen()
    }
}
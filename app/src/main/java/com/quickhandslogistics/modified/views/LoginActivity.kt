package com.quickhandslogistics.modified.views

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LoginContract
import com.quickhandslogistics.modified.presenters.LoginPresenter
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), LoginContract.View, View.OnClickListener {

    private lateinit var loginPresenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextPassword.transformationMethod = PasswordTransformationMethod()
        textViewForgotPassword.setOnClickListener(this)
        buttonLogin.setOnClickListener(this)

        loginPresenter = LoginPresenter(this, resources, sharedPref)
        loginPresenter.loadEmployeeId()
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenter.onDestroy()
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewForgotPassword.id -> {
                    startIntent(ForgotPasswordActivity::class.java, isFinish = false)
                }
                buttonLogin.id -> {
                    Utils.hideSoftKeyboard(activity)

                    val employeeLoginId = editTextEmployeeId.text.toString().trim()
                    val password = editTextPassword.text.toString().trim()

                    loginPresenter.validateLoginDetails(employeeLoginId, password)
                }
            }
        }
    }

    /** Presenter Listeners */
    override fun loadEmployeeId(employeeId: String) {
        editTextEmployeeId.setText(employeeId)
        editTextEmployeeId.setSelection(employeeId.length)
    }

    override fun showEmptyEmployeeIdError() {
        editTextEmployeeId.requestFocus()
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, resources.getString(R.string.text_employee_error_msg))
    }

    override fun showEmptyPasswordError() {
        editTextPassword.requestFocus()
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, resources.getString(R.string.text_password_err_msg))
    }

    override fun showInvalidPasswordError() {
        editTextPassword.requestFocus()
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, resources.getString(R.string.text_password_err_length))
    }

    override fun showAPIErrorMessage(message: String) {
        editTextEmployeeId.requestFocus()
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showNextScreen() {
        startIntent(DashBoardActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
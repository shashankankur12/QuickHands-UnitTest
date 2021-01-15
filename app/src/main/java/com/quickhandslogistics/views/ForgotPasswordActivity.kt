package com.quickhandslogistics.views

import android.os.Bundle
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.ForgotPasswordContract
import com.quickhandslogistics.presenters.ForgotPasswordPresenter
import com.quickhandslogistics.utils.*
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity(), ForgotPasswordContract.View, View.OnClickListener {

    private lateinit var forgotPasswordPresenter: ForgotPasswordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setupToolbar(getString(R.string.forgot_password))

        buttonPasswordReset.setOnClickListener(this)

        forgotPasswordPresenter = ForgotPasswordPresenter(this, resources)
    }

    override fun onDestroy() {
        super.onDestroy()
        forgotPasswordPresenter.onDestroy()
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        view?.let {
            when (view.id) {
                buttonPasswordReset.id -> {
                    AppUtils.hideSoftKeyboard(activity)

                    val employeePasswordResetId = editTextEmpId.text.toString().trim()
                    forgotPasswordPresenter.validatePasswordResetDetails(employeePasswordResetId)
                }
            }
        }
    }

    /** Presenter Listeners */
    override fun showEmptyEmployeeIdError() {
        editTextEmpId.requestFocus()
        SnackBarFactory.createSnackBar(activity, mainConstraintPasswordLayout, resources.getString(R.string.empty_employee_id_message))
    }

    override fun showAPIErrorMessage(message: String) {
        editTextEmpId.requestFocus()
        SnackBarFactory.createSnackBar(activity, mainConstraintPasswordLayout, message)
    }

    override fun showAPISuccessMessage(message: String) {
        CustomProgressBar.getInstance().showSuccessDialog(message, activity, object : CustomDialogListener {
            override fun onConfirmClick() {
                onBackPressed()
            }
        })
    }
}
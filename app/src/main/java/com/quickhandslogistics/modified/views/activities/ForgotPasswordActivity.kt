package com.quickhandslogistics.modified.views.activities

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.ForgotPasswordContract
import com.quickhandslogistics.modified.contracts.InfoDialogContract
import com.quickhandslogistics.modified.presenters.ForgotPasswordPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.fragments.InfoDialogFragment
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity(), ForgotPasswordContract.View, View.OnClickListener {

    private var progressDialog: Dialog? = null
    private lateinit var forgotPasswordPresenter: ForgotPasswordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setupToolbar(getString(R.string.string_forgot_password_heading))

        buttonPasswordReset.setOnClickListener(this)

        forgotPasswordPresenter = ForgotPasswordPresenter(this, resources)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonPasswordReset.id -> {
                    Utils.hideSoftKeyboard(activity)

                    val employeePasswordResetId = editTextEmpId.text.toString().trim()
                    forgotPasswordPresenter.validatePasswordResetDetails(employeePasswordResetId)
                }
            }
        }
    }

    override fun showEmptyEmployeeIdError() {
        editTextEmpId.requestFocus()
        SnackBarFactory.createSnackBar(
            activity,
            mainConstraintPasswordLayout,
            resources.getString(R.string.text_employee_error_msg)
        )
    }

    override fun showProgress(message: String) {
        progressDialog =
            CustomProgressBar.getInstance(activity).showProgressDialog(message)
    }

    override fun hideProgress() {
        progressDialog?.dismiss()
    }

    override fun showAPIErrorMessage(message: String) {
        editTextEmpId.requestFocus()
        SnackBarFactory.createSnackBar(
            activity,
            mainConstraintPasswordLayout,
            message
        )
    }

    override fun showAPISuccessMessage(message: String) {
        val dialog = InfoDialogFragment.newInstance(message,
            onClickListener = object : InfoDialogContract.View.OnClickListener {
                override fun onPositiveButtonClick() {
                }
            })
        dialog.show(supportFragmentManager, InfoDialogFragment::class.simpleName)
    }

    override fun onDestroy() {
        super.onDestroy()
        forgotPasswordPresenter.onDestroy()
    }
}
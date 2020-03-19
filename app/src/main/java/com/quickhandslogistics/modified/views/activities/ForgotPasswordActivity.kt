package com.quickhandslogistics.modified.views.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
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
        showMessageDialog(message)
    }

    override fun showAPISuccessMessage(message: String) {
        editTextEmpId.requestFocus()
        showMessageDialog(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        forgotPasswordPresenter.onDestroy()
    }

    fun showMessageDialog(message:String) {

        val dialogConfirm = Dialog(activity)

        dialogConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogConfirm.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogConfirm.window?.attributes?.windowAnimations = R.style.dialogAnimation
        dialogConfirm.setCancelable(false)
        dialogConfirm.setContentView(R.layout.dialog_forgot_password_message)

        val window = dialogConfirm.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val textOk = dialogConfirm.findViewById<TextView>(R.id.text_ok)
        val textmessage = dialogConfirm.findViewById<TextView>(R.id.text_message)
        textmessage.text = message

        textOk.setOnClickListener {
            dialogConfirm.dismiss()
        }
        dialogConfirm.show()
    }
}
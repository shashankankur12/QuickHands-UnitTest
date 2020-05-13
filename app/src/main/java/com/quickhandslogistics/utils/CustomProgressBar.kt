package com.quickhandslogistics.utils

import android.content.Context
import com.quickhandslogistics.R
import com.sahilgarg.forks.cn.pedant.SweetAlert.SweetAlertDialog

class CustomProgressBar {

    private var progressDialog: SweetAlertDialog? = null

    companion object {
        private var customProgressBar: CustomProgressBar? = null

        fun getInstance(): CustomProgressBar {
            if (customProgressBar == null) {
                customProgressBar = CustomProgressBar()
            }
            return customProgressBar as CustomProgressBar
        }
    }

    fun show(titleMessage: String = "", message: String = "", activityContext: Context) {
        progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.PROGRESS_TYPE)
        //  pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        progressDialog?.titleText = if (titleMessage.isEmpty()) activityContext.getString(R.string.loading) else titleMessage
        if (message.isNotEmpty()) {
            progressDialog?.contentText = message
        }
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    fun hide() {
        progressDialog?.dismissWithAnimation()
    }

    fun showInfoDialog(title: String = "", message: String, activityContext: Context) {
        val progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.NORMAL_TYPE)
        progressDialog.titleText = if (title.isEmpty()) activityContext.getString(R.string.info) else title
        progressDialog.contentText = message
        progressDialog.show()
    }

    fun showErrorDialog(message: String, activityContext: Context) {
        val progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.ERROR_TYPE)
        progressDialog.titleText = activityContext.getString(R.string.error)
        progressDialog.contentText = message
        progressDialog.show()
    }

    fun showSuccessDialog(message: String, activityContext: Context, listener: CustomDialogListener) {
        val progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.SUCCESS_TYPE)
        progressDialog.titleText = activityContext.getString(R.string.success)
        progressDialog.contentText = message
        progressDialog.setCancelable(false)
        progressDialog.setConfirmClickListener {
            it.dismiss()
            listener.onConfirmClick()
        }
        progressDialog.show()
    }

    fun showWarningDialog(message: String = "", activityContext: Context, listener: CustomDialogWarningListener) {
        val progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.WARNING_TYPE)
        progressDialog.titleText = activityContext.getString(R.string.are_you_sure)
        progressDialog.contentText = if (message.isEmpty()) activityContext.getString(R.string.you_want_make_these_changes) else message
        progressDialog.confirmText = activityContext.getString(R.string.string_yes)
        progressDialog.cancelText = activityContext.getString(R.string.string_no)
        progressDialog.showCancelButton(true)
        progressDialog.setConfirmClickListener {
            it.dismissWithAnimation()
            listener.onConfirmClick()
        }
        progressDialog.setCancelClickListener {
            it.dismissWithAnimation()
            listener.onCancelClick()
        }
        progressDialog.show()
    }
}

interface CustomDialogListener {
    fun onConfirmClick()
}

interface CustomDialogWarningListener {
    fun onConfirmClick()
    fun onCancelClick()
}
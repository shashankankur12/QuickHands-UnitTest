package com.quickhandslogistics.utils

import android.content.Context
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.quickhandslogistics.R

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
        progressDialog?.titleText =
            if (titleMessage.isEmpty()) activityContext.getString(R.string.loading) else titleMessage
        if (message.isNotEmpty()) {
            progressDialog?.contentText = message
        }
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    fun hide() {
        progressDialog?.dismissWithAnimation()
    }

    fun showInfoDialog(title: String, message: String, activityContext: Context) {
        val progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.NORMAL_TYPE)
        progressDialog.titleText = title
        progressDialog.contentText = message
        progressDialog.show()
    }

    fun showSuccessDialog(
        message: String, activityContext: Context, listener: CustomDialogSuccessListener
    ) {
        val progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.SUCCESS_TYPE)
        progressDialog.titleText = activityContext.getString(R.string.success)
        progressDialog.contentText = message
        progressDialog.setConfirmClickListener {
            listener.onConfirmClick()
        }
        progressDialog.show()
    }
}

interface CustomDialogSuccessListener {
    fun onConfirmClick()
}
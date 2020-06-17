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
            progressDialog?.contentText = message.capitalize()
        }
        progressDialog?.setCancelable(false)
        progressDialog?.show()
        progressDialog?.setTitleTextAppearance(R.style.dialogTitleTextViewTheme)
        progressDialog?.setContentTextAppearance(R.style.dialogContentTextViewTheme)
    }

    fun hide() {
        progressDialog?.dismissWithAnimation()
    }

    fun showInfoDialog(title: String = "", message: String, activityContext: Context) {
        val progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.NORMAL_TYPE)
        progressDialog.titleText = if (title.isEmpty()) activityContext.getString(R.string.info) else title
        progressDialog.contentText = message.capitalize()
        progressDialog.confirmText = activityContext.getString(R.string.ok)
        progressDialog.show()
        progressDialog.setTitleTextAppearance(R.style.dialogTitleTextViewTheme)
        progressDialog.setContentTextAppearance(R.style.dialogContentTextViewTheme)
        updateButtonsUI(progressDialog, activityContext)
    }

    fun showErrorDialog(message: String, activityContext: Context) {
        val progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.ERROR_TYPE)
        progressDialog.titleText = activityContext.getString(R.string.error)
        progressDialog.contentText = message.capitalize()
        progressDialog.confirmText = activityContext.getString(R.string.ok)
        progressDialog.show()
        progressDialog.setTitleTextAppearance(R.style.dialogTitleTextViewTheme)
        progressDialog.setContentTextAppearance(R.style.dialogContentTextViewTheme)
        updateButtonsUI(progressDialog, activityContext)
    }

    fun showSuccessDialog(message: String, activityContext: Context, listener: CustomDialogListener) {
        val progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.SUCCESS_TYPE)
        progressDialog.titleText = activityContext.getString(R.string.success)
        progressDialog.contentText = message.capitalize()
        progressDialog.confirmText = activityContext.getString(R.string.ok)
        progressDialog.setCancelable(false)
        progressDialog.setConfirmClickListener {
            it.dismissWithAnimation()
            listener.onConfirmClick()
        }
        progressDialog.show()
        progressDialog.setTitleTextAppearance(R.style.dialogTitleTextViewTheme)
        progressDialog.setContentTextAppearance(R.style.dialogContentTextViewTheme)
        updateButtonsUI(progressDialog, activityContext)
    }

    fun showSuccessOptionDialog(message: String, activityContext: Context, listener: CustomDialogWarningListener) {
        val progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.SUCCESS_TYPE)
        progressDialog.titleText = activityContext.getString(R.string.success)
        progressDialog.contentText = message.capitalize()
        progressDialog.confirmText = activityContext.getString(R.string.yes)
        progressDialog.cancelText = activityContext.getString(R.string.no)
        progressDialog.setCancelable(false)
        progressDialog.setConfirmClickListener {
            it.dismissWithAnimation()
            listener.onConfirmClick()
        }
        progressDialog.setCancelClickListener {
            it.dismissWithAnimation()
            listener.onCancelClick()
        }
        progressDialog.show()
        progressDialog.setTitleTextAppearance(R.style.dialogTitleTextViewTheme)
        progressDialog.setContentTextAppearance(R.style.dialogContentTextViewTheme)
        updateButtonsUI(progressDialog, activityContext)
    }

    fun showWarningDialog(message: String = "", activityContext: Context, listener: CustomDialogWarningListener) {
        val progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.WARNING_TYPE)
        progressDialog.titleText = activityContext.getString(R.string.are_you_sure_alert_message)
        progressDialog.contentText = if (message.isEmpty()) activityContext.getString(R.string.warning_alert_message) else message.capitalize()
        progressDialog.confirmText = activityContext.getString(R.string.yes)
        progressDialog.cancelText = activityContext.getString(R.string.no)
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
        progressDialog.setTitleTextAppearance(R.style.dialogTitleTextViewTheme)
        progressDialog.setContentTextAppearance(R.style.dialogContentTextViewTheme)
        updateButtonsUI(progressDialog, activityContext)
    }

    fun showLogoutDialog(message: String = "", activityContext: Context, listener: CustomDialogWarningListener) {
        val progressDialog = SweetAlertDialog(activityContext, SweetAlertDialog.WARNING_TYPE)
        progressDialog.titleText = activityContext.getString(R.string.are_you_sure_alert_message)
        progressDialog.contentText = if (message.isEmpty()) activityContext.getString(R.string.warning_alert_message) else message.capitalize()
        progressDialog.confirmText = activityContext.getString(R.string.logout_buttoon)
        progressDialog.cancelText = activityContext.getString(R.string.cancel)
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
        progressDialog.setTitleTextAppearance(R.style.dialogTitleTextViewTheme)
        progressDialog.setContentTextAppearance(R.style.dialogContentTextViewTheme)
        updateButtonsUI(progressDialog, activityContext)
    }

    private fun updateButtonsUI(progressDialog: SweetAlertDialog, activityContext: Context) {
        val density = activityContext.resources.displayMetrics.density
        val width: Int = (activityContext.resources.getDimension(R.dimen.dialogButtonWidth) / density).toInt()
        val height: Int = (activityContext.resources.getDimension(R.dimen.dialogButtonHeight) / density).toInt()

        progressDialog.setConfirmTextAppearance(R.style.dialogConfirmButtonTheme)
        progressDialog.setConfirmButtonBackground(R.drawable.round_button_red_selector)
        progressDialog.setConfirmButtonWidth(width)
        progressDialog.setConfirmButtonHeight(height)

        progressDialog.setCancelTextAppearance(R.style.dialogCancelButtonTheme)
        progressDialog.setCancelButtonBackground(R.drawable.round_button_negative_selector)
        progressDialog.setCancelButtonWidth(width)
        progressDialog.setCancelButtonHeight(height)
    }
}

interface CustomDialogListener {
    fun onConfirmClick()
}

interface CustomDialogWarningListener {
    fun onConfirmClick()
    fun onCancelClick()
}
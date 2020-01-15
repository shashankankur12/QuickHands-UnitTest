package com.quickhandslogistics.utils

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.TextView
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.dialog_forgot_password.*

class DialogHelper {

    companion object {
        fun showDialog(activity : Activity) {
            val dialog = Dialog(activity)
            dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog .setCancelable(false)
            dialog .setContentView(R.layout.dialog_forgot_password)

            dialog.text_ok.setOnClickListener {
                dialog.dismiss()
            }

            dialog .show()
        }
    }
}
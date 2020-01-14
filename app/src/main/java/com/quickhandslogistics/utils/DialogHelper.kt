package com.quickhandslogistics.utils

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.TextView
import com.quickhandslogistics.R

class DialogHelper {

    companion object {
        fun showDialog(title: String, activity : Activity) {
            val dialog = Dialog(activity)
            dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog .setCancelable(false)
            dialog .setContentView(R.layout.layout_dialog)

            dialog .show()
        }
    }
}
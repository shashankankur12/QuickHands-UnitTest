package com.quickhandslogistics.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import com.quickhandslogistics.R

class CustomProgressBar private constructor(context: Context) {

    companion object {
        private var customProgressBar: CustomProgressBar?= null
        private var mContext: Context ?= null

        fun getInstance(context: Context) : CustomProgressBar {
            mContext = context

            if (customProgressBar == null) {
                customProgressBar = CustomProgressBar(context)
            }
            return customProgressBar as CustomProgressBar
        }
    }

    fun showProgressDialog(progressMsg: String): Dialog {

            val dialog = Dialog(mContext!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val window = dialog.window
            window?.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            window!!.setContentView(R.layout.dialog_progress)

            dialog.setCancelable(false)
            dialog.show()

            val textProgress = dialog.findViewById<TextView>(R.id.text_progress)
            textProgress?.text = progressMsg

        return dialog
    }

    public fun hideDialog(dialog: Dialog) {
        if (dialog == null) return

        dialog.dismiss()
    }
}
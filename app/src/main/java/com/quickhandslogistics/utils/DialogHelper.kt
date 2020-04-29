package com.quickhandslogistics.utils

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.AppConstant.Companion.EDIT_DIALOG
import kotlinx.android.synthetic.main.dialog_add_notes.*

class DialogHelper {

    companion object {
        fun showNotesDialog(animation: Int, activity: Activity) {

            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            dialog.setContentView(R.layout.dialog_add_notes)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.attributes?.windowAnimations = animation

            dialog.edit_notes?.setText(SharedPref.getInstance().getString(EDIT_DIALOG))

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window?.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT

            dialog.window?.attributes = lp
            dialog.setCancelable(true)

            dialog.button_save.setOnClickListener(View.OnClickListener { view ->
                SharedPref.getInstance().setString(EDIT_DIALOG, dialog.edit_notes.text.toString())
                dialog.dismiss()
            })

            dialog.image_close.setOnClickListener(View.OnClickListener { view ->
                dialog.dismiss()
            })

            dialog.show()
        }
    }
}
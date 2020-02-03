package com.quickhandslogistics.utils

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.AdapterViewAnimator
import android.widget.EditText
import android.widget.TextView
import co.clicke.databases.SharedPreferenceHandler
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.dialog_add_notes.*
import kotlinx.android.synthetic.main.dialog_forgot_password.*

class DialogHelper {

    companion object {
        fun showDialog(title: String, activity: Activity) {
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.layout_dialog)

            dialog.text_ok.setOnClickListener {
                dialog.dismiss()
            }

            var text = dialog.findViewById<TextView>(R.id.text_message)
            var yes = dialog.findViewById<TextView>(R.id.text_yes)
            var no = dialog.findViewById<TextView>(R.id.text_no)

            text.text = title

            yes.setOnClickListener {
                dialog.dismiss()
            }

            no.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        fun showNotesDialog(animation: Int, activity: Activity) {

            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            dialog.setContentView(R.layout.dialog_add_notes)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.attributes?.windowAnimations = animation

            SharedPreferenceHandler.getInstance(activity)
            dialog?.edit_notes?.setText(SharedPreferenceHandler.getString("Edit"))

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog?.window?.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT

            dialog?.window?.attributes = lp
            dialog.setCancelable(true)

            dialog.button_save.setOnClickListener(View.OnClickListener { view ->
                SharedPreferenceHandler.setString("Edit", dialog.edit_notes.text.toString())
                dialog.dismiss()
            })

            dialog.image_close.setOnClickListener(View.OnClickListener { view ->
                dialog.dismiss()
            })

            dialog.show()
        }
    }
}
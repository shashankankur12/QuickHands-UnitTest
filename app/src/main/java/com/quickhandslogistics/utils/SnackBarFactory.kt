package com.quickhandslogistics.utils

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.quickhandslogistics.R

object SnackBarFactory {

    fun createSnackBar(context: Context, view: View, message: String) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val textView =
            snackBar.view.findViewById<TextView>((com.google.android.material.R.id.snackbar_text))
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        snackBar.show()
    }

    fun createSnackBar(
        context: Context,
        view: View,
        message: String,
        actionString: String,
        onClickListener: View.OnClickListener
    ) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        val textView =
            snackBar.view.findViewById<TextView>((com.google.android.material.R.id.snackbar_text))
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        snackBar.setAction(actionString, onClickListener)
        snackBar.show()
    }
}
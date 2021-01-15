package com.quickhandslogistics.utils

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

object SnackBarFactory {

    fun createShortSnackBar(context: Context, view: View, message: String, isShow: Boolean = true): Snackbar {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        val textView = snackBar.view.findViewById<TextView>((com.google.android.material.R.id.snackbar_text))
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        if (isShow) snackBar.show()
        return snackBar
    }

    fun createSnackBar(context: Context, view: View, message: String, isShow: Boolean = true): Snackbar {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val textView = snackBar.view.findViewById<TextView>((com.google.android.material.R.id.snackbar_text))
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        if (isShow) snackBar.show()
        return snackBar
    }

    fun createSnackBar(context: Context, view: View, message: String, actionString: String, onClickListener: View.OnClickListener) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        val textView = snackBar.view.findViewById<TextView>((com.google.android.material.R.id.snackbar_text))
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        snackBar.setAction(actionString, onClickListener)
        snackBar.show()
    }
}
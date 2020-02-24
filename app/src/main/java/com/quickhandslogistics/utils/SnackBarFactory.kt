package com.quickhandslogistics.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.quickhandslogistics.R

object SnackBarFactory {

    fun createSnackBar(context: Context?, view: View, message: String): Snackbar {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        val sbView = snackbar.view
        val textView = sbView.findViewById<View>((com.google.android.material.R.id.snackbar_text)) as TextView
        textView.setTextColor(context?.resources!!.getColor(R.color.colorWhite))
        snackbar.show()
        return snackbar
    }

    fun createSnackBarIndefinite(context: Context, view: View, message: String): Snackbar {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.show()

        return snackbar
    }

    fun createSnackBarMultiLine(context: Context, view: View, message: String): Snackbar {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        val snackBarTextView = snackbar.view.findViewById<View>((com.google.android.material.R.id.snackbar_text)) as TextView
        snackBarTextView.maxLines = 999
        snackbar.show()

        return snackbar
    }

    fun createSnackBarWithoutRoot(context: Activity, message: String) {
        Snackbar.make(context.getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG).show()
    }
}
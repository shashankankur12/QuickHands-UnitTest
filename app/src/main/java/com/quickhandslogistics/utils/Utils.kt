package com.quickhandslogistics.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.dialog_forgot_password.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {

        fun finishActivity(activity: Activity) {
            activity.finish()
            activity.overridePendingTransition(
                R.anim.anim_prev_slide_in,
                R.anim.anim_prev_slide_out
            )
        }

        fun getPoppinsRegularTypeface(context: Context): Typeface {
            return Typeface.createFromAsset(context.assets, "fonts/poppinsregular.ttf")
        }

        fun getPoppinsSemiBoldTypeface(context: Context): Typeface {
            return Typeface.createFromAsset(context.assets, "fonts/poppinssemibold.ttf.ttf")
        }

        fun navigateToActivity(currentActivity: Activity, destinationActivity: Activity) {
            currentActivity.startActivity(Intent(currentActivity, destinationActivity::class.java))
            currentActivity.overridePendingTransition(
                R.anim.anim_next_slide_in,
                R.anim.anim_next_slide_out
            )
        }

        fun askForPermissions(activity: Activity, permissions: String): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                permissions
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun showForgotPasswordDialog(animation: Int, title: String, activity: Activity) {
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.attributes?.windowAnimations = animation
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_forgot_password)
            dialog.setCancelable(true)

            dialog.text_ok.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        fun hideSoftKeyboard(activity: Activity) {
            try {
                val inputMethodManager = activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                if (Objects.requireNonNull(inputMethodManager).isActive) {
                    inputMethodManager.hideSoftInputFromWindow(
                        Objects.requireNonNull<View>(activity.currentFocus).windowToken, 0
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun showError(activity: Activity?, view: View, error: Any) {
            if (activity == null) return

            val errorManager = ErrorManager(activity, view, error)
            errorManager.handleErrorResponse()
        }

        fun convertLocalToUTC(inputTime: String): String {
            var formattedDate = ""
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val targetFormat = SimpleDateFormat("dd MMM yyyy")
            targetFormat.timeZone = TimeZone.getTimeZone("UTC")
            var date: Date? = null
            try {
                date = originalFormat.parse(inputTime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            formattedDate = targetFormat.format(date)
            return formattedDate
        }
    }
}
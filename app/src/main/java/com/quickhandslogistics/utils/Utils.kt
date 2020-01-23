package com.quickhandslogistics.utils

import android.R.color.white
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.dialog_forgot_password.*

class Utils {

    companion object {

        fun finishActivity(activity: Activity) {
            activity.finish()
            activity.overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
        }

        fun changeStatusBar(activity: Activity) {

            var window = activity.window
            val decor = window.decorView

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = activity.resources.getColor(white)
            } else {
                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = activity.resources.getColor(R.color.colorLightGrey)
            }
        }

        fun getPoppinsRegularTypeface(context : Context) : Typeface {
            return Typeface.createFromAsset(context.assets, "fonts/poppinsregular.ttf")
        }

        fun getPoppinsSemiBoldTypeface(context : Context) : Typeface {
            return Typeface.createFromAsset(context.assets, "fonts/poppinssemibold.ttf.ttf")
        }

        fun navigateToActivity(currentActivity: Activity, destinationActivity : Activity) {
            currentActivity.startActivity(Intent(currentActivity, destinationActivity::class.java))
            currentActivity.overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        }

        fun askForPermissions(activity: Activity, permissions: String) : Boolean {
            return ContextCompat.checkSelfPermission(activity, permissions) == PackageManager.PERMISSION_GRANTED
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

        fun Shake (view: View) : AnimatorSet {
            val animatorSet = AnimatorSet()

            val object1: ObjectAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.75f, 1.75f, 1.75f, 1f)
            val object2: ObjectAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.75f, 1.75f, 0.85f, 1f)

            animatorSet.playTogether(object1, object2)
            animatorSet.start()
            return animatorSet
        }
    }
}
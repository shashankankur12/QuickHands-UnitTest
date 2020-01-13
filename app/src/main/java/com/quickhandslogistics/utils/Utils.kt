package com.quickhandslogistics.utils

import android.R.color.black
import android.R.color.white
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import com.quickhandslogistics.R

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

        fun navigateToActivity(currentActivity: Activity, destinationActivity : Activity) {
            currentActivity.startActivity(Intent(currentActivity, destinationActivity::class.java))
            currentActivity.overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        }

        fun askForPermissions(activity: Activity, permissions: String) : Boolean {
            return ContextCompat.checkSelfPermission(activity, permissions) == PackageManager.PERMISSION_GRANTED
        }
    }
}
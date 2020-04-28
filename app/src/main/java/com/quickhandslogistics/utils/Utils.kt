package com.quickhandslogistics.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.quickhandslogistics.R
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
    }
}
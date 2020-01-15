package com.quickhandslogistics.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


class PermissionHelper(private val mActivity: Activity) {
    fun checkPermission(vararg permissions: String?): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    mActivity,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
        }
        return true
    }

}
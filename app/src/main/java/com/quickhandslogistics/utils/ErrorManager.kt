package com.quickhandslogistics.utils

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.network.NetworkConnectionInterceptor
import com.quickhandslogistics.view.activities.LoginActivity
import com.quickhandslogistics.model.error.Error


class ErrorManager(private val mActivity: Activity, private val mView: View, private val mObject: Any?) {

    fun handleErrorResponse() {
        if (mObject == null) {
            return
        }

        if (mObject is NetworkConnectionInterceptor.NoConnectivityException) {
            SnackBarFactory.createSnackBar(mActivity, mView, mActivity.resources.getString(R.string.no_internet))
        } else if (mObject is Throwable)
            SnackBarFactory.createSnackBar(mActivity, mView, mObject.localizedMessage!!)

        if (mObject is Error) {
            val response = mObject as Error?
            if (response?.error_message != null && response.error_message.isNotEmpty()) {

                if (response.error_message[0].equals("Your session has expired. Please login.", true)) {
                    doLogout()
                } else {
                    SnackBarFactory.createSnackBar(mActivity, mView, response.error_message[0])
                }
            }
        }
    }

    fun doLogout() {
        SnackBarFactory.createSnackBar(mActivity, mView, "Your session has expired. Please login.")

        Handler().postDelayed({
            //AppPreference.getInstance(mActivity).setBoolean(AppConstant.PREF_IS_LOGIN, false)
          //  AuthRepo.clearSessionId(mActivity)
            mActivity.startActivity(Intent(mActivity, LoginActivity::class.java)
                .putExtra("down", true)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))

            mActivity.overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
        }, 2000)
    }
}
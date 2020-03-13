package com.quickhandslogistics.modified.models

import android.os.Handler
import android.text.TextUtils
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_EMPLOYEE_ID
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_USER_EMAIL
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_USER_FULL_NAME
import com.quickhandslogistics.utils.SharedPref

class DashBoardModel(private val sharedPref: SharedPref) : DashBoardContract.Model {

    override fun fetchLeadProfileData(onFinishedListener: DashBoardContract.Model.OnFinishedListener) {
        if (!TextUtils.isEmpty(sharedPref.getString(PREFERENCE_USER_FULL_NAME))) {
            onFinishedListener.onLoadLeadProfile(
                sharedPref.getString(PREFERENCE_USER_FULL_NAME),
                sharedPref.getString(PREFERENCE_USER_EMAIL),
                sharedPref.getString(PREFERENCE_EMPLOYEE_ID)
            )
        }
    }
}
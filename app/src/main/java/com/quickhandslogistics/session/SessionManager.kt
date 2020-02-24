package com.quickhandslogistics.session

import android.util.Log
import com.quickhandslogistics.application.MyApplication
import com.quickhandslogistics.model.login.Data
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_EMAIL
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_IS_ACTIVE
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_IS_EMAIL_VERFD
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_IS_LOGIN
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_IS_PHONE_VERFD
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_ROLE
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_TOKEN_ID
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_USERFIRSTNAME
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_USERID
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_USERIMAGE
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_USERLASTNAME
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_USERPHONE
import com.quickhandslogistics.utils.AppPreference

object SessionManager : AppConstant {

    public fun setSession(loginData: Data) {

        AppPreference.getInstance().setString(PREF_TOKEN_ID, loginData.token)
        AppPreference.getInstance().setString(PREF_EMAIL, loginData.email)
        AppPreference.getInstance().setString(PREF_ROLE, loginData.role)
        //  AppPreference.getInstance().setString(PREF_USERIMAGE, loginData?.profile?.profile_image)
        AppPreference.getInstance().setString(PREF_USERFIRSTNAME, loginData.firstName)
        AppPreference.getInstance().setBoolean(PREF_IS_LOGIN, true)
        AppPreference.getInstance().setBoolean(PREF_IS_ACTIVE, loginData.isActive)
        AppPreference.getInstance().setBoolean(PREF_IS_EMAIL_VERFD, loginData.isEmailVerified)
        AppPreference.getInstance().setBoolean(PREF_IS_PHONE_VERFD, loginData.isPhoneVerified)
        AppPreference.getInstance().setString(PREF_USERLASTNAME, loginData.lastName)
        AppPreference.getInstance().setString(PREF_USERPHONE, loginData.phone)
        AppPreference.getInstance().setString(PREF_USERID, loginData.id)

    }

    fun getSessionId(): String? {
        return AppPreference.getInstance(MyApplication.application!!).getString(PREF_TOKEN_ID)
    }

    fun getUserId(): String? {
        return AppPreference.getInstance(MyApplication.application!!).getString(PREF_USERID)
    }

    fun hasSession(): Boolean {
        return getSessionId() != ""
    }

    fun clearSession() {
        //todo clear when logout
    }
}
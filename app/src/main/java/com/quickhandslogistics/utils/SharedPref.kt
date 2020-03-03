package com.quickhandslogistics.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.quickhandslogistics.application.MyApplication
import com.quickhandslogistics.utils.AppConstant.Companion.APP_SHARED_PREFERENCE
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_LANGUAGE_SELECT

class SharedPref private constructor(context: Context): AppConstant {

    var SharedPreferencesFileName: String = "MyPref"
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

   init {
        sharedPreferences = context.getSharedPreferences(SharedPreferencesFileName, 0)
        editor = sharedPreferences?.edit()
    }

    companion object {
        private var instance : SharedPref? = null

        fun  getInstance(): SharedPref {
            if (instance == null)
                instance = SharedPref(MyApplication.mApp!!)

            return instance!!
        }
    }

    fun clearAllShareperece() {
        editor?.clear()
        editor?.apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences!!.getBoolean(key, false)
    }

    fun setBoolean(key: String, loggedIn: Boolean) {
        editor!!.putBoolean(key, loggedIn).apply()
    }

    fun getString(key: String): String {
        return sharedPreferences!!.getString(key, "")!!
    }

    fun setString(key: String, value: String?) {
        editor!!.putString(key, value)
        editor!!.apply()
    }

    fun setInteger(activeJobPosition: Int, key: String) {
        editor!!.putInt(key, activeJobPosition).apply()
    }

    fun getInteger(key: String): Int {
        return sharedPreferences!!.getInt(key, 0)
    }

    fun clearSession() {
        editor!!.clear().commit()
    }

    fun getFirebaseToken(key: String): String? {
        return sharedPreferences!!.getString(key, null)
    }

    fun setFirebaseToken(key: String, token: String) {
        editor!!.putString(key, token).apply()
    }

    fun setLanguageSelected(language: String?) {
        if (editor != null) editor!!.putString(PREF_LANGUAGE_SELECT, language).apply()
    }

    fun getLanguageSelected(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(
            APP_SHARED_PREFERENCE,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(PREF_LANGUAGE_SELECT, "en-us")
    }
}
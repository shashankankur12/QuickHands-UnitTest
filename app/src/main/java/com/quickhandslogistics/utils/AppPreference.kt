package com.quickhandslogistics.utils

import android.content.Context
import android.content.SharedPreferences
import co.clicke.databases.SharedPreferenceHandler.editor
import com.quickhandslogistics.application.MyApplication
import com.quickhandslogistics.utils.AppConstant.Companion.APP_SHARED_PREFERENCE
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_KEY_USER_LOGGED_IN
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_KEY_USER_SESSION
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_SESSION_ID

class AppPreference  private constructor(context: Context) {

    private val mEditor: SharedPreferences.Editor
    private val APP_SHARED_PREFERENCE = "App_Preference"
    private lateinit var mSharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor? = null

    init {
        mSharedPreferences = context.getSharedPreferences(APP_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        mEditor = mSharedPreferences.edit()
    }


    fun getBoolean(key: String): Boolean {
        return mSharedPreferences.getBoolean(key, false)
    }

    fun setBoolean(key: String, loggedIn: Boolean) {
        mEditor.putBoolean(key, loggedIn).apply()
    }

    fun getString(key: String): String? {
        return mSharedPreferences.getString(key, "")
    }

    fun setString(key: String, sessionId: String) {
        mEditor.putString(key, sessionId)
        mEditor.apply()
    }

    fun setInteger(activeJobPosition: Int, key: String) {
        mEditor.putInt(key, activeJobPosition).apply()
    }

    fun getInteger(key: String): Int {
        return mSharedPreferences.getInt(key, 0)
    }

    fun clearSession() {
        mEditor.clear()
    }

    fun getFirebaseToken(key: String): String? {
        return mSharedPreferences.getString(key, null)
    }

    fun setFirebaseToken(key: String, token: String) {
        mEditor.putString(key, token)
    }

    fun getVibration(key: String): Boolean {
        return mSharedPreferences.getBoolean(key, false)
    }

    fun setVibration(key: String, IsVibrate: Boolean) {
        mEditor.putBoolean(key, IsVibrate).apply()
    }


    companion object {
        private var instance : AppPreference? = null
        private val KEY_USER_LOGGED_IN =PREF_KEY_USER_LOGGED_IN
        private val KEY_USER_SESSION = PREF_KEY_USER_SESSION

        fun  getInstance(context: Context = MyApplication.application!!): AppPreference {
            if (instance == null)
                instance = AppPreference(context)

            return instance!!
        }


        fun create(context: Context): AppPreference {
            val sharedPreferences =
                context.getSharedPreferences(APP_SHARED_PREFERENCE, Context.MODE_PRIVATE)
            val userSharedPreferences = AppPreference(context)
            userSharedPreferences.editor = sharedPreferences.edit()

            return userSharedPreferences
        }

        fun getLoginStatus(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences(APP_SHARED_PREFERENCE, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(KEY_USER_LOGGED_IN, false)
        }

        fun setloggedInStatus(loggedin: Boolean) {
            if (editor != null) {
                editor?.putBoolean(KEY_USER_LOGGED_IN, loggedin)
                editor?.apply()
            }
        }

        fun setSessionId(sessionId: String) {
            if (editor != null)
                editor?.putString(KEY_USER_SESSION, sessionId)?.apply()
        }

        fun getSessionId(context: Context): String? {
            val sharedPreferences =
                context.getSharedPreferences(APP_SHARED_PREFERENCE, Context.MODE_PRIVATE)
            return sharedPreferences.getString(KEY_USER_SESSION, "")
        }

        fun clearSessionId(context: Context) {
            AppPreference.getInstance(context).setString(PREF_SESSION_ID, "")
        }

    }
}
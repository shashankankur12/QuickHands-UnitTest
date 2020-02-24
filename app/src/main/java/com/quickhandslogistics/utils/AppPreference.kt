package com.quickhandslogistics.utils

import android.content.Context
import android.content.SharedPreferences
import com.quickhandslogistics.application.MyApplication

class AppPreference  private constructor(context: Context) {

    private val mEditor: SharedPreferences.Editor
    private val APP_SHARED_PREFERENCE = "App_Preference"
    private lateinit var mSharedPreferences: SharedPreferences

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

        fun  getInstance(context: Context = MyApplication.application!!): AppPreference {
            if (instance == null)
                instance = AppPreference(context)

            return instance!!
        }
    }
}
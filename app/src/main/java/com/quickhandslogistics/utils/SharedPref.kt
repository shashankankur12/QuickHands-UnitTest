package com.quickhandslogistics.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.quickhandslogistics.application.MyApplication
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_EMPLOYEE_ID
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_NAME
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_REGISTRATION_TOKEN

class SharedPref private constructor(context: Context) : AppConstant {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, 0)
    private var editor: SharedPreferences.Editor

    init {
        editor = sharedPreferences?.edit()
    }

    companion object {
        private var instance: SharedPref? = null

        fun getInstance(): SharedPref {
            if (instance == null)
                instance = SharedPref(MyApplication.mApp!!)

            return instance!!
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun setBoolean(key: String, loggedIn: Boolean) {
        editor.putBoolean(key, loggedIn).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue)!!
    }

    fun getClassObject(key: String, javaClass: Class<*>): Any? {
        val jsonString = getString(key)
        return Gson().fromJson(jsonString, javaClass)
    }

    fun setString(key: String, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getInteger(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun setInteger(activeJobPosition: Int, key: String) {
        editor.putInt(key, activeJobPosition).apply()
    }

    fun setClassObject(key: String, classObject: Any) {
        val jsonString = Gson().toJson(classObject)
        setString(key, jsonString)
    }

    fun performLogout() {
        // Save the previous employeeId on Logout
        val employeeId = getString(PREFERENCE_EMPLOYEE_ID)
        val token = getString(PREFERENCE_REGISTRATION_TOKEN)
        val language = getString(AppConstant.PREFERENCE_LANGUAGE, AppConstant.LANGUAGE_ENGLISH_CODE)
        editor.clear().apply()
        setString(PREFERENCE_EMPLOYEE_ID, employeeId)
        setString(PREFERENCE_REGISTRATION_TOKEN, token)
        setString(AppConstant.PREFERENCE_LANGUAGE, language)
    }
}
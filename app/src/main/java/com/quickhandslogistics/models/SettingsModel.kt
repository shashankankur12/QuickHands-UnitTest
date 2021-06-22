package com.quickhandslogistics.models

import android.content.res.Resources
import android.util.Log
import com.franmontiel.localechanger.LocaleChanger
import com.quickhandslogistics.contracts.SettingsContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SettingsModel(private val resources: Resources, private val sharedPref: SharedPref) : SettingsContract.Model {
    override fun saveSelectedLanguage(selectedLanguage: String, listener: SettingsContract.Model.OnFinishedListener) {
        DataManager.getService().changeLanguage(DataManager.getAuthToken()).enqueue(object :
            Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (DataManager.isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), listener)) {
                    sharedPref.setString(AppConstant.PREFERENCE_LANGUAGE, selectedLanguage)
                    listener.restartActivity(selectedLanguage)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(LoginModel::class.simpleName, t.localizedMessage!!)
                listener.onFailure()
            }
        })
//        sharedPref.setString(AppConstant.PREFERENCE_LANGUAGE, selectedLanguage)
//        LocaleChanger.setLocale(Locale(selectedLanguage))
//        listener.restartActivity(selectedLanguage)
    }

    override fun saveNotificationState(
        checked: Boolean,
        listener: SettingsContract.Model.OnFinishedListener
    ) {
        sharedPref.setBoolean(AppConstant.PREFERENCE_NOTIFICATION, checked)
    }

    override fun checkSelectedSettings(listener: SettingsContract.Model.OnFinishedListener) {
        val selectedLanguage = sharedPref.getString(
            AppConstant.PREFERENCE_LANGUAGE,
            defaultValue = AppConstant.LANGUAGE_ENGLISH_CODE
        )
        val notificationEnabled = sharedPref.getBoolean(
            AppConstant.PREFERENCE_NOTIFICATION,
            defaultValue = true
        )
        listener.showSelectedSettings(selectedLanguage, notificationEnabled)
    }
}
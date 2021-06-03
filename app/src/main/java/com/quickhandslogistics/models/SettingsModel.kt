package com.quickhandslogistics.models

import android.content.res.Resources
import com.franmontiel.localechanger.LocaleChanger
import com.quickhandslogistics.contracts.SettingsContract
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class SettingsModel(private val resources: Resources, private val sharedPref: SharedPref) : SettingsContract.Model {

    override fun saveSelectedLanguage(
        selectedLanguage: String,
        listener: SettingsContract.Model.OnFinishedListener
    ) {
        sharedPref.setString(AppConstant.PREFERENCE_LANGUAGE, selectedLanguage)
        LocaleChanger.setLocale(Locale(selectedLanguage))
        listener.restartActivity(selectedLanguage)
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
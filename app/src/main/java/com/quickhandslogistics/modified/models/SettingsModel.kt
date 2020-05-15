package com.quickhandslogistics.modified.models

import android.content.res.Resources
import com.franmontiel.localechanger.LocaleChanger
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.setting.SettingContract
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class SettingsModel(private val resources: Resources, private val sharedPref: SharedPref) : SettingContract.Model {

    override fun saveSelectedLanguage(selectedLanguage: String, listener: SettingContract.Model.OnFinishedListener) {
        sharedPref.setString(AppConstant.PREFERENCE_LANGUAGE, selectedLanguage)
        LocaleChanger.setLocale(Locale(selectedLanguage))
        listener.restartActivity()
    }

    override fun saveNotificationState(checked: Boolean, listener: SettingContract.Model.OnFinishedListener) {
        sharedPref.setBoolean(AppConstant.PREFERENCE_NOTIFICATION, checked)
    }

    override fun checkSelectedSettings(listener: SettingContract.Model.OnFinishedListener) {
        val selectedLanguage = sharedPref.getString(AppConstant.PREFERENCE_LANGUAGE, defaultValue = resources.getString(R.string.english))
        val notificationEnabled = sharedPref.getBoolean(AppConstant.PREFERENCE_NOTIFICATION, defaultValue = true)
        listener.showSelectedSettings(selectedLanguage, notificationEnabled)
    }
}
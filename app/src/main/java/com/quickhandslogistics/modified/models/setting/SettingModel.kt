package com.quickhandslogistics.modified.models.setting

import com.franmontiel.localechanger.LocaleChanger
import com.quickhandslogistics.modified.contracts.setting.SettingContract
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class SettingModel(private val sharedPref: SharedPref) : SettingContract.Model {

    override fun saveSelectedLanguage(
        selectedLanguage: String,
        listener: SettingContract.Model.OnFinishedListener
    ) {
        sharedPref.setString(AppConstant.PREFERENCE_LANGUAGE, selectedLanguage)
        LocaleChanger.setLocale(Locale(selectedLanguage))
        listener.restartActivity()
    }

    override fun checkSelectedLanguage(listener: SettingContract.Model.OnFinishedListener) {
        val selectedLanguage = sharedPref.getString(AppConstant.PREFERENCE_LANGUAGE)
        listener.showSelectedLanguage(selectedLanguage)
    }
}
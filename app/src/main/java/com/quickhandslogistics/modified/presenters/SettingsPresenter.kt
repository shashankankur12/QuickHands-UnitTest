package com.quickhandslogistics.modified.presenters

import android.content.res.Resources
import com.quickhandslogistics.modified.contracts.setting.SettingContract
import com.quickhandslogistics.modified.models.SettingsModel
import com.quickhandslogistics.utils.SharedPref

class SettingsPresenter(
    private var settingView: SettingContract.View?,
    resources: Resources, sharedPref: SharedPref
) : SettingContract.Presenter, SettingContract.Model.OnFinishedListener {

    private val settingsModel: SettingsModel = SettingsModel(resources, sharedPref)

    /*
    *Presenter Interfaces
    */
    override fun onDestroy() {
        settingView = null
    }

    override fun checkSelectedSettings() {
        settingsModel.checkSelectedSettings(this)
    }

    override fun saveSelectedLanguage(selectedLanguage: String) {
        settingsModel.saveSelectedLanguage(selectedLanguage, this)
    }

    override fun changeNotificationState(checked: Boolean) {
        settingsModel.saveNotificationState(checked, this)
    }

    /*
    *Model Result Interfaces
    */
    override fun restartActivity() {
        settingView?.restartActivity()
    }

    override fun showSelectedSettings(selectedLanguage: String, notificationEnabled: Boolean) {
        settingView?.showSelectedSettings(selectedLanguage, notificationEnabled)
    }
}



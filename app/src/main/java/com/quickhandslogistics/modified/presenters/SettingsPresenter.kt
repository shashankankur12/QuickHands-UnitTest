package com.quickhandslogistics.modified.presenters

import android.content.res.Resources
import com.quickhandslogistics.modified.contracts.SettingsContract
import com.quickhandslogistics.modified.models.SettingsModel
import com.quickhandslogistics.utils.SharedPref

class SettingsPresenter(
    private var settingsView: SettingsContract.View?, resources: Resources, sharedPref: SharedPref
) : SettingsContract.Presenter, SettingsContract.Model.OnFinishedListener {

    private val settingsModel = SettingsModel(resources, sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        settingsView = null
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

    /** Model Result Listeners */
    override fun restartActivity() {
        settingsView?.restartActivity()
    }

    override fun showSelectedSettings(selectedLanguage: String, notificationEnabled: Boolean) {
        settingsView?.showSelectedSettings(selectedLanguage, notificationEnabled)
    }
}



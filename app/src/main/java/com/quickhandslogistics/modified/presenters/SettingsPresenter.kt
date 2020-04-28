package com.quickhandslogistics.modified.presenters

import com.quickhandslogistics.modified.contracts.setting.SettingContract
import com.quickhandslogistics.modified.models.SettingsModel
import com.quickhandslogistics.utils.SharedPref

class SettingsPresenter(
    private var settingView: SettingContract.View?,
    sharedPref: SharedPref
) : SettingContract.Presenter, SettingContract.Model.OnFinishedListener {

    private val settingsModel: SettingsModel =
        SettingsModel(sharedPref)

    /*
    *Presenter Interfaces
    */
    override fun onDestroy() {
        settingView = null
    }

    override fun checkSelectedLanguage() {
        settingsModel.checkSelectedLanguage(this)
    }

    override fun saveSelectedLanguage(selectedLanguage: String) {
        settingsModel.saveSelectedLanguage(selectedLanguage, this)
    }

    /*
    *Model Result Interfaces
    */
    override fun restartActivity() {
        settingView?.restartActivity()
    }

    override fun showSelectedLanguage(selectedLanguage: String) {
        settingView?.showSelectedLanguage(selectedLanguage)
    }
}



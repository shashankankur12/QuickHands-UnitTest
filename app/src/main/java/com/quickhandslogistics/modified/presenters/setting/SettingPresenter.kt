package com.quickhandslogistics.modified.presenters.setting

import com.quickhandslogistics.modified.contracts.setting.SettingContract
import com.quickhandslogistics.modified.models.setting.SettingModel
import com.quickhandslogistics.utils.SharedPref

class SettingPresenter(
    private var settingView: SettingContract.View?,
    sharedPref: SharedPref
) : SettingContract.Presenter, SettingContract.Model.OnFinishedListener {

    private val settingModel: SettingModel = SettingModel(sharedPref)

    /*
    *Presenter Interfaces
    */
    override fun onDestroy() {
        settingView = null
    }

    override fun checkSelectedLanguage() {
        settingModel.checkSelectedLanguage(this)
    }

    override fun saveSelectedLanguage(selectedLanguage: String) {
        settingModel.saveSelectedLanguage(selectedLanguage, this)
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



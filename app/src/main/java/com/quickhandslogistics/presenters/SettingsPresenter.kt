package com.quickhandslogistics.presenters

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.SettingsContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.models.SettingsModel
import com.quickhandslogistics.utils.SharedPref

class SettingsPresenter(
    private var settingsView: SettingsContract.View?,private val resources: Resources, sharedPref: SharedPref
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
    override fun restartActivity(selectedLanguage: String) {
        settingsView?.restartActivity(selectedLanguage)
    }

    override fun onFailure(message: String) {
        if (TextUtils.isEmpty(message)) {
            settingsView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            settingsView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        onFailure(errorCode.message)
    }

    override fun showSelectedSettings(selectedLanguage: String, notificationEnabled: Boolean) {
        settingsView?.showSelectedSettings(selectedLanguage, notificationEnabled)
    }
}



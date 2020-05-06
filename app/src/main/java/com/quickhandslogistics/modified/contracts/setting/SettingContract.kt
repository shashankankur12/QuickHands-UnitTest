package com.quickhandslogistics.modified.contracts.setting

import com.quickhandslogistics.modified.presenters.SettingsPresenter

class SettingContract {
    interface Model {
        fun checkSelectedSettings(listener: OnFinishedListener)
        fun saveSelectedLanguage(selectedLanguage: String, listener: OnFinishedListener)
        fun saveNotificationState(checked: Boolean, listener: OnFinishedListener)

        interface OnFinishedListener {
            fun showSelectedSettings(selectedLanguage: String, notificationEnabled: Boolean)
            fun restartActivity()
        }
    }

    interface View {
        fun showSelectedSettings(selectedLanguage: String, notificationEnabled: Boolean)
        fun restartActivity()
    }

    interface Presenter {
        fun onDestroy()
        fun checkSelectedSettings()
        fun saveSelectedLanguage(selectedLanguage: String)
        fun changeNotificationState(checked: Boolean)
    }
}
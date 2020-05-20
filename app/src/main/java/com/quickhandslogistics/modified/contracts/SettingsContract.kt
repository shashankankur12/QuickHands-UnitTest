package com.quickhandslogistics.modified.contracts

class SettingsContract {
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

    interface Presenter : BaseContract.Presenter {
        fun checkSelectedSettings()
        fun saveSelectedLanguage(selectedLanguage: String)
        fun changeNotificationState(checked: Boolean)
    }
}
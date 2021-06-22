package com.quickhandslogistics.contracts

class SettingsContract {
    interface Model {
        fun checkSelectedSettings(listener: OnFinishedListener)
        fun saveSelectedLanguage(selectedLanguage: String, listener: OnFinishedListener)
        fun saveNotificationState(checked: Boolean, listener: OnFinishedListener)

        interface OnFinishedListener: BaseContract.Model.OnFinishedListener {
            fun showSelectedSettings(selectedLanguage: String, notificationEnabled: Boolean)
            fun restartActivity(selectedLanguage: String)
        }
    }

    interface View : BaseContract.View{
        fun showSelectedSettings(selectedLanguage: String, notificationEnabled: Boolean)
        fun restartActivity(selectedLanguage: String)
        fun showAPIErrorMessage(message: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun checkSelectedSettings()
        fun saveSelectedLanguage(selectedLanguage: String)
        fun changeNotificationState(checked: Boolean)
    }
}
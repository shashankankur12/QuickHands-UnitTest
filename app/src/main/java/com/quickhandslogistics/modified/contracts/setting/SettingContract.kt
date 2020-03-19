package com.quickhandslogistics.modified.contracts.setting

class SettingContract {
    interface Model {
        fun checkSelectedLanguage(listener: OnFinishedListener)
        fun saveSelectedLanguage(selectedLanguage: String, listener: OnFinishedListener)

        interface OnFinishedListener {
            fun showSelectedLanguage(selectedLanguage: String)
            fun restartActivity()
        }
    }

    interface View {
        fun showSelectedLanguage(selectedLanguage: String)
        fun restartActivity()
    }

    interface Presenter {
        fun onDestroy()
        fun checkSelectedLanguage()
        fun saveSelectedLanguage(selectedLanguage: String)
    }
}
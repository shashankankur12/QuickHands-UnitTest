package com.quickhandslogistics.modified.contracts.setting


class SettingContract {
    interface Model {

        fun getLocale(listener: OnFinishedListener)
        fun saveSelectedLanguage(selectedLanguage: String, listener: OnFinishedListener)

        interface OnFinishedListener{
            fun showSelectedLocale(currentLocale: String)
            fun restartActivity()
        }
    }

    interface View {
        fun showSelectedLocale(currentLocale: String)
        fun restartActivity()
    }

    interface Presenter {
        fun onDestroy()
        fun getLocale()
        fun  setLanguageData(type :String)
        fun saveSelecedLanguage(selectedLanguage: String)
    }
}
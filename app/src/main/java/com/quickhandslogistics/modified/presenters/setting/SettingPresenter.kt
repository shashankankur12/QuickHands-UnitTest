package com.quickhandslogistics.modified.presenters.setting

import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.franmontiel.localechanger.LocaleChanger
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.quickhandslogistics.modified.contracts.setting.SettingContract
import com.quickhandslogistics.modified.models.setting.SettingModel
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_LANGUAGE
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class SettingPresenter(
private var settingView: SettingContract.View?,
private val sharedPref: SharedPref,
var fragmentActivity: FragmentActivity? = null
) : SettingContract.Presenter, SettingContract.Model.OnFinishedListener {
    override fun restartActivity() {
        settingView?.restartActivity()
    }

    override fun saveSelecedLanguage(selectedLanguage: String) {
        settingModel.saveSelectedLanguage(selectedLanguage, this)
    }

    override fun showSelectedLocale(currentLocale: String) {
        settingView?.showSelectedLocale(currentLocale)
    }

    private val settingModel: SettingModel = SettingModel(sharedPref)

    override fun onDestroy() {
        settingView = null
    }

    override fun getLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            settingModel.getLocale(this)
        }
    }

    override fun setLanguageData(selectedLanguage: String) {
        sharedPref.setString(PREFERENCE_LANGUAGE, selectedLanguage)
        LocaleChanger.setLocale(Locale(selectedLanguage))
        ActivityRecreationHelper.recreate(fragmentActivity!!, false)
    }
}



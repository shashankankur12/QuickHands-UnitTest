package com.quickhandslogistics.modified.views.fragments.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.setting.SettingContract
import com.quickhandslogistics.modified.presenters.setting.SettingPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(), SettingContract.View,
    View.OnClickListener {

    private lateinit var settingPresenter: SettingPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingPresenter = SettingPresenter(this, sharedPref)
    }

    fun openChangeDialog(checkedId: Int) {
        Utils.dialogChangeLanguage(R.style.dialogAnimation, activity!!, object : Utils.IOnClick {
            override fun onConfirm() {
                when (checkedId) {
                    R.id.radioBtnEnglish -> {
                        setLanguageData(getString(R.string.english))
                    }
                    R.id.radioBtnSpanish -> {
                        setLanguageData(getString(R.string.spanish))
                    }
                }
            }

            override fun onDismiss() {
                if(checkedId.equals(R.id.radioBtnEnglish)) {
                    radioBtnSpanish.isChecked = true
                    setLanguageData(getString(R.string.spanish))
                } else {
                    radioBtnEnglish.isChecked = true
                    setLanguageData(getString(R.string.english))
                }
            }
        })
    }

    fun setLanguageData(selectedLanguage: String) {
        settingPresenter.saveSelecedLanguage(selectedLanguage)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                radioBtnEnglish.id -> {
                    openChangeDialog(R.id.radioBtnEnglish)
                }

                radioBtnSpanish.id -> {
                    openChangeDialog(R.id.radioBtnSpanish)
                }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        settingPresenter.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioBtnEnglish.setOnClickListener(this)
        radioBtnSpanish.setOnClickListener(this)

        settingPresenter.getLocale()
    }

    override fun restartActivity() {
        ActivityRecreationHelper.recreate(fragmentActivity!!, false)
    }

    override fun showSelectedLocale(currentLocale: String) {
        if (currentLocale.equals(R.string.english))
            radioBtnEnglish.isChecked = true
        else
            radioBtnSpanish.isChecked = true
    }
}

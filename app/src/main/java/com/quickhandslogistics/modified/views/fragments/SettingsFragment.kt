package com.quickhandslogistics.modified.views.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.franmontiel.localechanger.LocaleChanger
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_LANGUAGE
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*

class SettingsFragment : BaseFragment() {
    var currentLocale: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    private fun getLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocale = sharedPref.getString(PREFERENCE_LANGUAGE)

            //  radioBtnEnglish.isChecked = currentLocale!!.equals(ESPANOL)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getLocale()

        radioBtnEnglish.setOnClickListener {
            openChangeDialog()
        }

        radioBtnSpanish.setOnClickListener {
            openChangeDialog()
        }
    }

    private fun openChangeDialog() {
        Utils.dialogChangeLanguage(R.style.dialogAnimation, activity!!, object : Utils.IOnClick {
            override fun onConfirm() {

                radioBtnEnglish.isChecked = !radioBtnEnglish.isChecked

                if (currentLocale!! != getString(R.string.spanish)) {
                    setLanguageData(getString(R.string.spanish))
                } else {
                    setLanguageData(getString(R.string.english))
                }
            }

            override fun onDismiss() {
                radioBtnEnglish.isChecked = radioBtnEnglish.isChecked
            }
        })
    }

    fun setLanguageData(selectedLanguage: String) {
        sharedPref.setString(PREFERENCE_LANGUAGE, selectedLanguage)
        LocaleChanger.setLocale(Locale(selectedLanguage))
        ActivityRecreationHelper.recreate(fragmentActivity!!, false)
    }
}

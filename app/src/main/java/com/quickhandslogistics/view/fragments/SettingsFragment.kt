package com.quickhandslogistics.view.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.clicke.databases.SharedPreferenceHandler
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.AppConstant.Companion.CHECKED
import com.quickhandslogistics.utils.AppConstant.Companion.ENGLISH
import com.quickhandslogistics.utils.AppConstant.Companion.ESPANOL
import com.quickhandslogistics.utils.AppConstant.Companion.LANGUAGE
import com.quickhandslogistics.utils.LanguageManager
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*

class SettingsFragment : Fragment() {
    var currentLocale: String?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    fun getLocale() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
             currentLocale = resources.configuration.locale.language

             if (currentLocale!!.equals(ESPANOL)) {
                 switch_language.isChecked = true
             } else {
                 switch_language.isChecked = false
             }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SharedPreferenceHandler.getInstance(activity!!)

        getLocale()

        linear_switch.setOnClickListener {
            openChangeDialog()
        }
    }

    fun openChangeDialog() {
        Utils.dialogChangeLanguage(R.style.dialogAnimation, activity!!, object : Utils.IOnClick {
            override fun onConfirm() {
                 switch_language.isChecked = !switch_language.isChecked

                if (!currentLocale!!.equals(ESPANOL, true)) {
                    SharedPreferenceHandler.setString(LANGUAGE, "(English)")
                    setLanguageData(ESPANOL)
                } else {
                    SharedPreferenceHandler.setString(LANGUAGE, "(Español)")
                    setLanguageData(ENGLISH)
                }
            }

            override fun onDismiss() {
                switch_language.isChecked = switch_language.isChecked
            }
        })
    }

    fun setLanguageData(language: String?) {
        SharedPreferenceHandler.setLanguageSelected(language)
        LanguageManager.setLanguage(activity, language)
        val intent = activity!!.intent
        activity!!.finish()
        activity!!.startActivity(intent)
    }
}

package com.quickhandslogistics.view.fragments

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import co.clicke.databases.SharedPreferenceHandler
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.LanguageManager
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*


class SettingsFragment : Fragment() {
    var isChecked:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SharedPreferenceHandler.getInstance(activity!!)
        changeLanguageCheck()
        switch_language.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if(p1) {
                    SharedPreferenceHandler.setBoolean("checked", p1)
                    SharedPreferenceHandler.setString("language", "(english)")
                    setLanguageData("es")
                } else {
                    SharedPreferenceHandler.setBoolean("checked", p1)
                    SharedPreferenceHandler.setString("language", "(español)")
                    setLanguageData("en-us")
                }
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

    fun changeLanguageCheck() {
        switch_language.isChecked = SharedPreferenceHandler.getBoolean("checked")
        text_espanol.text = SharedPreferenceHandler.getString("language")
    }
}

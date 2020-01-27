package com.quickhandslogistics.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.CompoundButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import co.clicke.databases.SharedPreferenceHandler
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.AppConstant.Companion.CHECKED
import com.quickhandslogistics.utils.AppConstant.Companion.ENGLISH
import com.quickhandslogistics.utils.AppConstant.Companion.ESPANOL
import com.quickhandslogistics.utils.AppConstant.Companion.LANGUAGE
import com.quickhandslogistics.utils.LanguageManager
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.dialog_change_language.*
import kotlinx.android.synthetic.main.dialog_forgot_password.*
import kotlinx.android.synthetic.main.fragment_settings.*
import okhttp3.internal.Util

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
                    val dialog = Dialog(activity!!)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.window?.attributes?.windowAnimations = R.style.dialogAnimation
                    dialog.setCancelable(true)
                    dialog.setContentView(R.layout.dialog_change_language)

                    var textYes = dialog.findViewById<TextView>(R.id.text_yes)
                    var textNo = dialog.findViewById<TextView>(R.id.text_no)

                    textYes.setOnClickListener {
                        SharedPreferenceHandler.setBoolean(CHECKED, p1)
                        SharedPreferenceHandler.setString(LANGUAGE, "(English)")
                        setLanguageData(ESPANOL)
                    }

                    textNo.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()

                } else {

                    val dialog = Dialog(activity!!)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.window?.attributes?.windowAnimations = R.style.dialogAnimation
                    dialog.setCancelable(true)
                    dialog.setContentView(R.layout.dialog_change_language)

                    var textYes = dialog.findViewById<TextView>(R.id.text_yes)
                    var textNo = dialog.findViewById<TextView>(R.id.text_no)
                    textYes.setOnClickListener {
                        SharedPreferenceHandler.setBoolean(CHECKED, p1)
                        SharedPreferenceHandler.setString(LANGUAGE, "(Español)")
                        setLanguageData(ENGLISH)
                    }

                    textNo.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
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
        switch_language.isChecked = SharedPreferenceHandler.getBoolean(CHECKED)
        text_espanol.text = SharedPreferenceHandler.getString(LANGUAGE)
    }
}

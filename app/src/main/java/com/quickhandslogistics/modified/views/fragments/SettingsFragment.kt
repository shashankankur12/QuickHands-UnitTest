package com.quickhandslogistics.modified.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.InfoDialogContract
import com.quickhandslogistics.modified.contracts.setting.SettingContract
import com.quickhandslogistics.modified.presenters.setting.SettingPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(), SettingContract.View,
    View.OnClickListener {

    private lateinit var settingPresenter: SettingPresenter
    private lateinit var selectedLanguage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingPresenter = SettingPresenter(this, sharedPref)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioBtnEnglish.setOnClickListener(this)
        radioBtnSpanish.setOnClickListener(this)

        settingPresenter.checkSelectedLanguage()
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                radioBtnEnglish.id -> {
                    if (selectedLanguage != getString(R.string.english))
                        shopwConfirmationDialog(getString(R.string.english))
                }
                radioBtnSpanish.id -> {
                    if (selectedLanguage != getString(R.string.spanish))
                        shopwConfirmationDialog(getString(R.string.spanish))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        settingPresenter.onDestroy()
    }

    override fun restartActivity() {
        ActivityRecreationHelper.recreate(fragmentActivity!!, false)
    }

    override fun showSelectedLanguage(selectedLanguage: String) {
        this.selectedLanguage = selectedLanguage
        if (selectedLanguage == getString(R.string.english))
            radioBtnEnglish.isChecked = true
        else
            radioBtnSpanish.isChecked = true
    }

    private fun shopwConfirmationDialog(language: String) {
        val dialog = InfoDialogFragment.newInstance(
            getString(R.string.string_language_dialog),
            positiveButtonText = getString(R.string.string_yes),
            negativeButtonText = getString(R.string.string_no),
            onClickListener = object : InfoDialogContract.View.OnClickListener {
                override fun onPositiveButtonClick() {
                    settingPresenter.saveSelectedLanguage(language)
                }

                override fun onNegativeButtonClick() {
                    if (language == getString(R.string.english)) {
                        radioBtnSpanish.isChecked = true
                    } else {
                        radioBtnEnglish.isChecked = true
                    }
                }
            })
        dialog.show(childFragmentManager, InfoDialogFragment::class.simpleName)
    }

}

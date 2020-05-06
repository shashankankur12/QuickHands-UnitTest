package com.quickhandslogistics.modified.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.setting.SettingContract
import com.quickhandslogistics.modified.presenters.SettingsPresenter
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(), SettingContract.View, View.OnClickListener {

    private var settingsPresenter: SettingsPresenter? = null
    private lateinit var selectedLanguage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsPresenter = SettingsPresenter(this, resources, sharedPref)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioBtnEnglish.setOnClickListener(this)
        radioBtnSpanish.setOnClickListener(this)
        switchNotification.setOnClickListener(this)
        textViewNotificationInfo.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        settingsPresenter?.checkSelectedSettings()
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                radioBtnEnglish.id -> {
                    if (selectedLanguage != getString(R.string.english))
                        showConfirmationDialog(getString(R.string.english))
                    else {
                    }
                }
                radioBtnSpanish.id -> {
                    if (selectedLanguage != getString(R.string.spanish))
                        showConfirmationDialog(getString(R.string.spanish))
                    else {
                    }
                }
                switchNotification.id -> {
                    settingsPresenter?.changeNotificationState(switchNotification.isChecked)
                }
                textViewNotificationInfo.id -> {
                    val intent = Intent()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, fragmentActivity?.packageName)
                    } else {
                        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                        intent.putExtra("app_package", fragmentActivity?.packageName)
                        intent.putExtra("app_uid", fragmentActivity?.applicationInfo?.uid)
                    }
                    startActivity(intent)
                }
                else -> {
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        settingsPresenter?.onDestroy()
    }

    override fun restartActivity() {
        ActivityRecreationHelper.recreate(fragmentActivity!!, false)
    }

    override fun showSelectedSettings(selectedLanguage: String, notificationEnabled: Boolean) {
        this.selectedLanguage = selectedLanguage
        if (selectedLanguage == getString(R.string.english))
            radioBtnEnglish.isChecked = true
        else
            radioBtnSpanish.isChecked = true

        // Check if Notifications are disabled from system settings
        val notificationSystemSettingsEnabled =
            NotificationManagerCompat.from(fragmentActivity!!).areNotificationsEnabled()
        if (notificationSystemSettingsEnabled) {
            switchNotification.isChecked = notificationEnabled
            switchNotification.isEnabled = true
            textViewNotificationInfo.visibility = View.GONE
        } else {
            switchNotification.isChecked = false
            switchNotification.isEnabled = false
            textViewNotificationInfo.visibility = View.VISIBLE
        }
    }

    private fun showConfirmationDialog(language: String) {
        CustomProgressBar.getInstance().showWarningDialog(
            getString(R.string.string_language_dialog),
            fragmentActivity!!, object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    settingsPresenter?.saveSelectedLanguage(language)
                }

                override fun onCancelClick() {
                    if (language == getString(R.string.english)) {
                        radioBtnSpanish.isChecked = true
                    } else {
                        radioBtnEnglish.isChecked = true
                    }
                }
            })
    }
}

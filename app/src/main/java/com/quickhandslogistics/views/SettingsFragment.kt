package com.quickhandslogistics.views

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
import com.quickhandslogistics.contracts.SettingsContract
import com.quickhandslogistics.presenters.SettingsPresenter
import com.quickhandslogistics.utils.AppConstant.Companion.LANGUAGE_ENGLISH_CODE
import com.quickhandslogistics.utils.AppConstant.Companion.LANGUAGE_SPANISH_CODE
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(), SettingsContract.View, View.OnClickListener {

    private lateinit var selectedLanguage: String

    private var settingsPresenter: SettingsPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsPresenter = SettingsPresenter(this, resources, sharedPref)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

    override fun onDestroy() {
        super.onDestroy()
        settingsPresenter?.onDestroy()
    }

    private fun showConfirmationDialog(language: String) {
        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.language_change_alert_message), fragmentActivity!!, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                settingsPresenter?.saveSelectedLanguage(language)
            }

            override fun onCancelClick() {
                if (language == LANGUAGE_ENGLISH_CODE) {
                    radioBtnSpanish.isChecked = true
                } else {
                    radioBtnEnglish.isChecked = true
                }
            }
        })
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                radioBtnEnglish.id -> {
                    if (selectedLanguage != LANGUAGE_ENGLISH_CODE) showConfirmationDialog(LANGUAGE_ENGLISH_CODE) else {
                    }
                }
                radioBtnSpanish.id -> {
                    if (selectedLanguage != LANGUAGE_SPANISH_CODE) showConfirmationDialog(LANGUAGE_SPANISH_CODE) else {
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

    /** Presenter Listeners */
    override fun restartActivity() {
        ActivityRecreationHelper.recreate(fragmentActivity!!, false)
    }

    override fun showSelectedSettings(selectedLanguage: String, notificationEnabled: Boolean) {
        this.selectedLanguage = selectedLanguage
        if (selectedLanguage == LANGUAGE_ENGLISH_CODE)
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
}
package com.quickhandslogistics.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.SplashActivity
import com.quickhandslogistics.modified.views.schedule.ScheduleDetailActivity
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_IS_FUTURE_DATE
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULE_IDENTITY
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref

class NotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        SharedPref.getInstance().setString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val authToken = SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN)

        // Check if user is logged in or not
        if (authToken.isNotEmpty()) {
            val notificationSystemSettingsEnabled = NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()
            val notificationEnabled = SharedPref.getInstance().getBoolean(AppConstant.PREFERENCE_NOTIFICATION, defaultValue = true)

            if (notificationSystemSettingsEnabled && notificationEnabled) {
                var notificationTitle = getString(R.string.app_name)
                var notificationContent = ""
                var notificationType = ""
                if (!message.data.isNullOrEmpty()) {
                    if (message.data.containsKey(AppConstant.NOTIFICATION_KEY_TITLE)) {
                        notificationTitle = message.data[AppConstant.NOTIFICATION_KEY_TITLE].toString()
                    }
                    if (message.data.containsKey(AppConstant.NOTIFICATION_KEY_CONTENT)) {
                        notificationContent = message.data[AppConstant.NOTIFICATION_KEY_CONTENT].toString()
                    }
                    if (message.data.containsKey(AppConstant.NOTIFICATION_KEY_TYPE)) {
                        notificationType = message.data[AppConstant.NOTIFICATION_KEY_TYPE].toString()
                    }
                    createNotification(
                        notificationTitle, notificationContent, notificationType, message.data
                    )
                }
            }
        }
    }

    private fun createNotification(notificationTitle: String, notificationContent: String, notificationType: String, data: MutableMap<String, String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(notificationTitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationContent))
            .setAutoCancel(true)

        val intent = Intent(applicationContext, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

        // Check for different Notification Type and extract relevant data.
        val bundle = Bundle()
        if (notificationType == AppConstant.NOTIFICATION_TYPE_SCHEDULE_CREATE) {
            if (data.containsKey(AppConstant.NOTIFICATION_KEY_SCHEDULE_IDENTITY) && data.containsKey(AppConstant.NOTIFICATION_KEY_SCHEDULE_FROM_DATE)) {
                val scheduleIdentity = data[AppConstant.NOTIFICATION_KEY_SCHEDULE_IDENTITY].toString()
                val scheduleFromDate = data[AppConstant.NOTIFICATION_KEY_SCHEDULE_FROM_DATE].toString()
                val scheduleTime = DateUtils.getMillisecondsFromDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, scheduleFromDate)

                bundle.putBoolean(ARG_ALLOW_UPDATE, DateUtils.isCurrentDate(scheduleTime))
                bundle.putBoolean(ARG_IS_FUTURE_DATE, DateUtils.isFutureDate(scheduleTime))
                bundle.putString(ARG_SCHEDULE_IDENTITY, scheduleIdentity)
                bundle.putLong(ARG_SELECTED_DATE_MILLISECONDS, scheduleTime)
                intent.setClass(applicationContext, ScheduleDetailActivity::class.java)
            }
            intent.putExtras(bundle)
        }

        val pendingIntent = PendingIntent.getActivity(applicationContext, System.currentTimeMillis().toInt(), intent, 0)
        notification.setContentIntent(pendingIntent)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.let {
            val channel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
            if (channel == null) {
                val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "QuickHandsChannelId"
        private const val NOTIFICATION_CHANNEL_NAME = "Schedule Changes"
    }
}
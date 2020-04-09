package com.quickhandslogistics.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.activities.SplashActivity

class NotificationService : FirebaseMessagingService() {

    private val CHANNELID = "notification"
    var replyLabel = "Enter your reply here"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        createNotification(message)
    }

    private fun createNotification(message: RemoteMessage) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = NotificationCompat.Builder(this, CHANNELID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setAutoCancel(true)

        val intent = Intent(applicationContext, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        notification.setContentIntent(pendingIntent)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationChannel =
            NotificationChannel(CHANNELID, "Sample", NotificationManager.IMPORTANCE_LOW)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (notificationManager != null) {
            val channel = notificationManager.getNotificationChannel(CHANNELID)

            // Check & Delete the existing Notification channel if created with old configuration(IMPORTANCE_DEFAULT).
            if (channel != null && channel.importance != NotificationManager.IMPORTANCE_LOW) {
                notificationManager.deleteNotificationChannel(CHANNELID)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
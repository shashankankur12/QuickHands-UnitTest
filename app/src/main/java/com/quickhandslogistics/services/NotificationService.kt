package com.quickhandslogistics.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.quickhandslogistics.R
import com.quickhandslogistics.view.activities.SplashActivity

class NotificationService : FirebaseMessagingService() {

    private val CHANNELID = "notification"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        createNotification(message)
    }

    private fun createNotification(message : RemoteMessage) {

        var notification = NotificationCompat.Builder(this, CHANNELID)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle(message.notification?.title?: "QuickHands")
            .setContentText(message.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val intent = Intent(applicationContext, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        notification.setContentIntent(pendingIntent)

        val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification.build())
    }
}
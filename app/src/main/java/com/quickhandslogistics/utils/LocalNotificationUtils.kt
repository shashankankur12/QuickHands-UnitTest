package com.quickhandslogistics.utils

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
import com.quickhandslogistics.R
import com.quickhandslogistics.views.DashBoardActivity

object LocalNotificationUtils {

    private const val NOTIFICATION_CHANNEL_ID = "QuickHandsLocal"
    private const val NOTIFICATION_CHANNEL_NAME = "Local Updates"

    fun showTimeClockNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(context.getString(R.string.time_clock_fill_alert_title))
            .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.time_clock_fill_alert_message)))
            .setAutoCancel(true)

        val intent = Intent(context, DashBoardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

        val bundle = Bundle()
        bundle.putString(DashBoardActivity.ARG_SHOW_TAB_NAME, context.getString(R.string.time_clock))
        intent.putExtras(bundle)

        val pendingIntent = PendingIntent.getActivity(context, System.currentTimeMillis().toInt(), intent, 0)
        notification.setContentIntent(pendingIntent)

        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.let {
            val channel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
            if (channel == null) {
                val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }
}


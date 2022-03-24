package com.ezteam.applocker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ezteam.applocker.R
import com.ezteam.applocker.activity.SplashActivity

@RequiresApi(api = Build.VERSION_CODES.O)
object NotificationUtil {
    private const val NOTIFICATION_CHANNEL_ID = "10101"

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotification(mContext: Service, title: String?, message: String?) {
        val resultIntent = Intent(mContext, SplashActivity::class.java)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            112 /* Request code */,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel =
            NotificationChannel(NOTIFICATION_CHANNEL_ID, "App lock background task ", importance)
        mNotificationManager.createNotificationChannel(notificationChannel)
        val mBuilder = NotificationCompat.Builder(mContext)
        mBuilder.setSmallIcon(R.drawable.notifylock)
        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
        mBuilder.setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(false)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(resultPendingIntent)
        mContext.startForeground(145, mBuilder.build())
    }

    fun cancelNotification(mContext: Service) {
        val mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(145)
    }
}
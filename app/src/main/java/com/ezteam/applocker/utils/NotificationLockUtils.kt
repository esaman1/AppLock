package com.ezteam.applocker.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.ezteam.applocker.R
import com.ezteam.applocker.model.ItemNotificationModel

object NotificationLockUtils {
    const val DETAIL = "KEY_DETAIL"
    val listNotification = mutableListOf<ItemNotificationModel>()

    fun createNotification(service: Service) {
        var notificationManager: NotificationManager? = null
        notificationManager = service.getSystemService(
            NotificationListenerService.NOTIFICATION_SERVICE) as NotificationManager
        // createChanel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("No", "Lock", importance)
            mChannel.setSound(null, null)
            mChannel.description = "No"
            notificationManager = service.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(mChannel)
        }
        //
        val remoteView = RemoteViews(service.packageName, R.layout.layout_lock_notification)
        buildNotification(remoteView, service)
    }

    //buil notification
    private fun buildNotification(remoteView: RemoteViews, service: Service) {
        val notification = NotificationCompat.Builder(service, "No")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_notify)
            .setContent(remoteView)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        pendingIntent(remoteView, service)
        service.startForeground(1, notification)
    }

    private fun pendingIntent(remoteView: RemoteViews, service: Service) {
        val intentDetail = Intent(service.resources.getString(R.string.action_detail))
        val pendingIntent =
            PendingIntent.getBroadcast(service, 0, intentDetail, PendingIntent.FLAG_UPDATE_CURRENT)
        remoteView.setOnClickPendingIntent(R.id.layout_detail, pendingIntent)
    }

}
package com.ezteam.applocker.service

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.ezteam.applocker.R
import com.ezteam.applocker.activity.AppLockActivity
import com.ezteam.applocker.activity.MainLockActivity
import com.ezteam.applocker.broadcast.DetailNotificationBroadCast
import com.ezteam.applocker.key.KeyApp
import com.ezteam.applocker.model.ItemNotificationModel
import com.ezteam.applocker.utils.NotificationLockUtils
import com.ezteam.applocker.viewmodel.NotifyViewModel
import com.ezteam.baseproject.extensions.launchActivity
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject


class NotificationListener : NotificationListenerService() {
    private var connected = false
    private var broadCastDetail: DetailNotificationBroadCast? = null
    private val viewModel by inject<NotifyViewModel>()

    //    private val broadCastNotificationListenerService = BroadCastNotificationManager()
    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn!=null) {
            val itemDataNotification = ItemNotificationModel(
                sbn.notification.extras.getInt(Notification.EXTRA_SMALL_ICON),
                sbn.notification.extras.getString(Notification.EXTRA_TITLE),
                sbn.notification.extras.getString(Notification.EXTRA_TEXT),
                sbn.postTime,
                sbn.packageName,
                sbn.key,
                sbn.id,
                sbn.notification.contentIntent,
                sbn.isClearable
            )
            val listAppLock = Hawk.get(KeyApp.KEY_APP_LOCK, mutableListOf<String>())
            if (listAppLock.isNotEmpty()&&!checkDuplicate(itemDataNotification)&&checkLockNotification(
                    listAppLock
                )
            ) {
                for (item in listAppLock) {
                    if (item==itemDataNotification.packageName) {
                        viewModel.listNotifyLiveData.value = itemDataNotification
                        // clear notification
                        itemDataNotification.key?.let {
                            clearNotificationForKey(it)
                        }
                    }
                }
            }
        }
    }

    private fun checkDuplicate(itemDataNotification: ItemNotificationModel): Boolean {
        for (item in NotificationLockUtils.listNotification) {
            if (item==itemDataNotification) {
                return true
            }
        }
        return false
    }

    private fun checkLockNotification(list: MutableList<String>): Boolean {
        for (i in list) {
            if (i=="null") {
                return true
            }
        }
        return false
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        backMainActivity()
        connected = true
        registerBroadCast()
        viewModel.isLockNotification.observeForever {
            if (connected&&it) {
                NotificationLockUtils.createNotification(this)
            } else {
                stopForeground(true)
            }
        }
    }

    private fun backMainActivity() {
        if (AppLockActivity.isNotifyPermission) {
            launchActivity<MainLockActivity> { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            AppLockActivity.isNotifyPermission = false
        }
    }

    private fun registerBroadCast() {
        val intent = IntentFilter(resources.getString(R.string.action_detail))
        if (broadCastDetail==null) {
            broadCastDetail = DetailNotificationBroadCast()
        }
        registerReceiver(broadCastDetail, intent)
    }

    private fun unRegisterBroadCast() {
        unregisterReceiver(broadCastDetail)
    }

    private fun clearNotificationForKey(key: String) {
        if (connected) {
            this.cancelNotification(key)
        }
    }

    override fun onListenerDisconnected() {
        try {
            connected = false
            unRegisterBroadCast()
            super.onListenerDisconnected()
        } catch (e: IllegalArgumentException) {

        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }


}
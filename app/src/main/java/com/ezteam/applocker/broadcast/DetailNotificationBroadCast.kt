package com.ezteam.applocker.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ezteam.applocker.R
import com.ezteam.applocker.activity.DetailNotificationActivity
import com.ezteam.applocker.service.LockService

class DetailNotificationBroadCast : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        p1?.let { intent ->
            p0?.let { context ->
                if (intent.action==context.resources.getString(R.string.action_detail)) {
                    LockService.showDetailNotify = true
                    collapseStatusBar(context)
                    val intentDetail =
                        Intent(context, DetailNotificationActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                    context.startActivity(intentDetail)
                }
            }
        }
    }

    private fun collapseStatusBar(mContext: Context) {
        try {
            @SuppressLint("WrongConstant") val service = mContext.getSystemService("statusbar")
            val statusbarManager = Class.forName("android.app.StatusBarManager")
            val expand = statusbarManager.getMethod("collapsePanels")
            expand.invoke(service)
        } catch (ex: Exception) {
        }
    }
}
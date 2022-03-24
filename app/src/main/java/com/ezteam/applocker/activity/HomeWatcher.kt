package com.ezteam.applocker.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.ezteam.applocker.interfaces.OnHomePressedListener


class HomeWatcher(private val mContext: Context) {
    private val mFilter: IntentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
    private var mListener: OnHomePressedListener? = null
    private var mRecevier: InnerRecevier? = null
    fun setOnHomePressedListener(listener: OnHomePressedListener?) {
        mListener = listener
        mRecevier = InnerRecevier()
    }

    fun startWatch() {
        if (mRecevier != null) {
            mContext.registerReceiver(mRecevier, mFilter)
        }
    }

    fun stopWatch() {
        if (mRecevier != null) {
            mContext.unregisterReceiver(mRecevier)
        }
    }

    internal inner class InnerRecevier : BroadcastReceiver() {
        private val SYSTEM_DIALOG_REASON_KEY = "reason"
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
                if (reason != null) {
                    if (mListener != null) {
                        mListener!!.onHomePressed()
                    }
                }
            }
        }
    }

}
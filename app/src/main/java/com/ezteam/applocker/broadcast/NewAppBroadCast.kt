package com.ezteam.applocker.broadcast

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import com.ezteam.applocker.key.KeyLock
import com.ezteam.applocker.widget.ViewDialogLockNewApp
import com.ezteam.baseproject.utils.PreferencesUtils

class NewAppBroadCast : BroadcastReceiver() {
    private var alertDialog: Dialog? = null
    var listenerNewApp: ((String) -> Unit)? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { intents ->
            context?.let { context ->
                if (PreferencesUtils.getBoolean(KeyLock.LOCK_NEW_APP, false)) {
                    val uid = intent.getIntExtra(Intent.EXTRA_UID, -1)
                    if (uid!=-1) {
                        context.packageManager.getNameForUid(uid)?.let { pkg ->
                            alertDialog = Dialog(context)
                            val viewDialog = ViewDialogLockNewApp(context, null)
                            viewDialog.setApp(pkg)
                            viewDialog.binding.ok.setOnClickListener {
//                                PreferencesUtils.putString(KeyApp.NEW_APP, pkg)
                                listenerNewApp?.invoke(pkg)
                                alertDialog?.dismiss()
                            }
                            viewDialog.binding.cancel.setOnClickListener {
                                alertDialog?.dismiss()
                            }
                            alertDialog?.setCancelable(true)
                            alertDialog?.setCanceledOnTouchOutside(true)
                            alertDialog?.window?.setBackgroundDrawable(
                                ColorDrawable(Color.TRANSPARENT)
                            )
                            alertDialog?.setContentView(viewDialog.binding.root)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                alertDialog?.window!!.setType(
                                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                                )
                            else
                                alertDialog?.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)

                            if (Build.VERSION.SDK_INT >= 23&&Settings.canDrawOverlays(context)) {
                                alertDialog?.show()
                            } else {
                                alertDialog?.show()
                            }
                        }
                    }
                }
            }
        }
    }
}
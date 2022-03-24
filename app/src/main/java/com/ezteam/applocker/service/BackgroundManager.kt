package com.ezteam.applocker.service

import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ezteam.applocker.R
import com.ezteam.applocker.activity.LockActivity
import com.ezteam.applocker.item.ItemEnterPin
import com.ezteam.applocker.key.KeyApp
import com.ezteam.applocker.key.KeyLock
import com.ezteam.applocker.service.BackgroundManager.startForegroundServices
import com.ezteam.applocker.utils.NotificationLockUtils
import com.ezteam.applocker.utils.Provider
import com.ezteam.baseproject.utils.PreferencesUtils
import com.orhanobut.hawk.Hawk
import com.tailoredapps.biometricauth.BiometricAuth
import org.koin.core.component.KoinApiExtension


object BackgroundManager {
    const val REQUEST_CODE_OVERLAY = 1

    fun isServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name==service.service.className) {
                return true
            }
        }
        return false
    }

    fun startService(
        serviceClass: Class<*>,
        context: Context,
        activity: AppCompatActivity? = null
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           startForegroundServices(serviceClass, context)
        } else context.startService(Intent(context, serviceClass))
        activity?.let {
            Provider.fpManager =
                BiometricAuth.create(activity, useAndroidXBiometricPrompt = false)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startForegroundServices(serviceClass: Class<*>, context: Context) {
        context.startForegroundService(Intent(context, serviceClass))
    }

    fun requestPermission(
        activity: AppCompatActivity,
        isCreate: Boolean = true,
    ) {
        if (!isServiceRunning(LockService::class.java, activity)) {
            Provider.fpManager =
                BiometricAuth.create(activity, useAndroidXBiometricPrompt = false)
            startService(LockService::class.java, activity, activity)
        }
    }

    fun startServiceAndUsageData(
        activity: AppCompatActivity,
        isCreate: Boolean = true,
        isDetailNotify: Boolean = false
    ) {
        if (!isServiceRunning(LockService::class.java, activity)) {
            Provider.fpManager =
                BiometricAuth.create(activity, useAndroidXBiometricPrompt = false)
            startService(LockService::class.java, activity, activity)
        } else if (!isAccessGranted(activity)) {
            openUsageStats(activity)
        }
        if (!isCreate) {
            val intent = Intent(activity, LockActivity::class.java)
            intent.putExtra(KeyApp.LOCK_MY_APP, true)
            intent.putExtra(NotificationLockUtils.DETAIL, isDetailNotify)
            intent.putExtra(
                KeyLock.PKG_APP, "com.ezteam.applocker"
            )
            activity.startActivity(intent)
            activity.finish()
        }
    }

    fun checkPermission(context: Context): Boolean {
        if (!isAccessGranted(context)) {
            return false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                return false
            }
            if (isAccessGranted(context)&&Settings.canDrawOverlays(context)) {
                return true
            }
        } else {
            if (isAccessGranted(context)) {
                return true
            }
        }
        return false
    }

    @KoinApiExtension
    fun openUsageStats(context: Context) {
        LockService.isRequestPermission = true
        val intent = Intent("android.settings.USAGE_ACCESS_SETTINGS")
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse("package:" + context.packageName)
        try {
            context.startActivity(intent)
        } catch (unused: Exception) {
            intent.data = Uri.fromParts("package", context.packageName, null)
            try {
                context.startActivity(intent)
            } catch (unused2: Exception) {
                try {
                    intent.data = null
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.can_not_navigate_there),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun isAccessGranted(context: Context): Boolean {
        return try {
            val packageManager: PackageManager = context.packageManager
            val applicationInfo: ApplicationInfo =
                packageManager.getApplicationInfo(context.packageName, 0)
            val appOpsManager: AppOpsManager =
                context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            var mode = 0
            mode = appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid,
                applicationInfo.packageName
            )
            mode==AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun checkCreatePassword(context: Context): Boolean {
        when (PreferencesUtils.getString(KeyLock.LOCK, context.getString(R.string.pattern))) {
            context.getString(R.string.pattern) -> {
                if (!Hawk.get(KeyLock.KEY_PATTERN, (mutableListOf<Int>())).isNullOrEmpty()) {
                    return true
                }
            }
            context.getString(R.string.pin) -> {
                if (!Hawk.get(KeyLock.KEY_PIN, mutableListOf<ItemEnterPin>()).isNullOrEmpty()) {
                    return true
                }
            }
        }
        return false
    }
}
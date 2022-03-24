package com.ezteam.applocker.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ezteam.applocker.R
import com.ezteam.baseproject.activity.BaseActivity
import com.tailoredapps.biometricauth.BiometricAuth
import com.tailoredapps.biometricauth.BiometricAuthenticationCancelledException
import com.tailoredapps.biometricauth.BiometricAuthenticationException
import org.koin.core.component.KoinApiExtension
import java.text.SimpleDateFormat
import kotlin.math.round

object AppUtil {
    const val NOT_SUPPORT = 0
    const val NO_ANY_FINGERPRINT = 1
    const val HAVE_SUPPORT = 2
    val listPkgSettingFinal = mutableListOf(
        "settings",
        "vending",
        "gallery3d",
        "calendar",
        "contacts",
        "messaging",
        "camera",
        "dialer"
    )
    val listPkgConcernedApp = mutableListOf(
        "applocksecurity.recentapps",
        "com.facebook.orca",
        "com.android.mms",
        "com.tencent.mm",
        "com.miui.gallery",
        "com.android.gallery3d",
        "com.sec.android.gallery3d",
        "com.whatsapp",
        "com.facebook.katana",
        "jp.naver.line.android",
        "com.instagram.android",
        "com.twitter.android",
        "com.kakao.talk",
        "com.google.android.apps.plus",
        "com.google.android.contacts",
        "com.google.android.dialer",
        "com.google.android.apps.photos",
        "com.google.android.apps.messaging",
        "com.google.android.talk",
        "com.lge.email",
        "com.android.email",
        "com.kakao.talk",
        "com.sec.android.widgetapp.diotek.smemo",
        "com.google.android.apps.inbox",
        "com.paypal.android.p2pmobile",
        "com.skype.raider",
        "com.google.android.apps.fireball",
        "org.telegram.messenger",
        "mobi.drupe.app;com.android.contacts",
        "com.snapchat.android",
        "com.spotify.music",
        "com.coloros.gallery3d",
        "com.vivo.gallery",
        "com.google.android.youtube",
        "com.zhiliaoapp.musically",
        "com.google.android.gm"
    )

    @SuppressLint("SimpleDateFormat")
    fun convertDuration(duration: Long): String {
        return if (duration >= 3600000)
            SimpleDateFormat("hh:mm:ss").format(duration)
        else
            SimpleDateFormat("mm:ss").format(duration)
    }

    fun Double.toUnitMemory(): String {
        return when {
            (this >= 0&&this < 1000) -> {
                //byte
                "${round((this / (1 * 100.0))) / 10}Byte"
            }
            (this >= 1000.0&&this < 1000000.0) -> {
                //kp
                "${round((this / (1000 * 100.0))) / 10}KB"
            }
            this >= 1000000.0 -> {
                //gb
                "${round((this / (1000000 * 100.0))) / 10}GB"
            }
            else -> {
                "0KB"
            }
        }
    }

    @KoinApiExtension
    fun initFingerprint(app: AppCompatActivity, succeeded: (Boolean) -> Unit) {
        val biometricAuth = BiometricAuth.create(app, useAndroidXBiometricPrompt = true)
        if (!biometricAuth.hasFingerprintHardware) {
            //The devices does not support fingerprint authentication (i.e. has no fingerprint hardware)
            (app as BaseActivity<*>).toast(app.getString(R.string.can_not_use_finger_print))
        } else if (!biometricAuth.hasFingerprintsEnrolled) {
            //The user has not enrolled any fingerprints (i.e. fingerprint authentication is not activated by the user)
            (app as BaseActivity<*>).toast(app.getString(R.string.dont_have_fingerprint))
        } else {
            biometricAuth
                .authenticate(
                    title = "Please authenticate",
                    subtitle = "'Awesome Feature' requires your authentication",
                    description = "'Awesome Feature' exposes data private to you, which is why you need to authenticate.",
                    negativeButtonText = "Cancel",
                    prompt = "Touch the fingerprint sensor",
                    notRecognizedErrorText = "Not recognized"
                )
                .subscribe(
                    {
//                        PreferencesUtils.putInteger(KeyLock.TIME_ERROR, 0)
//                        passWordCorrect()
                        app.runOnUiThread {
                            succeeded.invoke(true)
                        }
                    },
                    { throwable ->
                        when (throwable) {
                            is BiometricAuthenticationCancelledException -> {
                                Log.d("BiometricAuth", "User cancelled the operation")
                            }
                            is BiometricAuthenticationException -> {
                                // Might want to check throwable.errorMessageId for fields in BiometricConstants.Error,
                                // to get more information about the error / further actions here.
                                Log.d("BiometricAuth", "Unrecoverable authentication error")
                            }
                            else -> {
                                Log.d("BiometricAuth", "Error during user authentication.")
                            }
                        }
                    }
                )
        }
    }

}
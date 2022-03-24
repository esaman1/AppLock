package com.ezteam.applocker.utils

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator


object VibrationUtil {
    fun startVibration(context: Context) {
        val v: Vibrator? = context.getSystemService(VIBRATOR_SERVICE) as Vibrator?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v?.vibrate(350)
        }
    }
}
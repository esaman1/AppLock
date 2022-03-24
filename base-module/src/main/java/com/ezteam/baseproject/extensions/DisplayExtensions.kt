package com.ezteam.baseproject.extensions

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

fun Context.getDisplayMetrics(): DisplayMetrics {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val metrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(metrics)
    return metrics
}
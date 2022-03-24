package com.ezteam.baseproject.extensions

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

val Fragment.packageManager: PackageManager
    get() = requireContext().packageManager

fun Fragment.showError(error: Throwable?) {
}

fun Fragment.getHeightStatusBar(activity: Activity): Int {
    var result = 0
    val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = activity.resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun Fragment.getHeightNavigationBar(activity: Activity): Int {
    var result = 0
    val resourceId = activity.resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = activity.resources.getDimensionPixelSize(resourceId)
    }
    return result
}
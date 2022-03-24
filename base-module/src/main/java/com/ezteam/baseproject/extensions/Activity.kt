package com.ezteam.baseproject.extensions

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.showError(error: Throwable?) {
}

fun AppCompatActivity.getHeightStatusBar(): Int {
    var result = 0
    val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = this.resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun AppCompatActivity.getHeightNavigationBar(): Int {
    var result = 0
    val resourceId = this.resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = this.resources.getDimensionPixelSize(resourceId)
    }
    return result
}
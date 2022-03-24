package com.ezteam.applocker.item

import android.graphics.drawable.Drawable

data class ItemAppLock(
    val resId: Drawable?,
    val name: String,
    val description: String?,
    var isLocked: Boolean = false,
    val packageName: String?,
    var isSys : Int = 0
)

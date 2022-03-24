package com.ezteam.applocker.item

import android.graphics.drawable.Drawable

data class ItemLockTheme(
    var folder: String,
    var themPattern: Drawable?,
    var themPin: Drawable?,
    var isSelected: Boolean = false,
    val isAds: Boolean = false
)
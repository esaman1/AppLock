package com.ezteam.applocker.item

import android.graphics.drawable.Drawable

data class ItemLockScreen(
    var folder: String,
    var photo: Drawable?,
    var isSelected: Boolean,
    var isAds: Boolean = false
)
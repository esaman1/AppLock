package com.ezteam.applocker.item

import android.graphics.drawable.Drawable

data class ItemNumber(
    val number: Int,
    var themeDefault: Drawable? = null,
    var themDown: Drawable? = null
)
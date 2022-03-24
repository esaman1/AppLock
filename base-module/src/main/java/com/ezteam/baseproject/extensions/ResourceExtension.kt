package com.ezteam.baseproject.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat

/**
 * Set typography to this TextView
 *
 * @param resId font style resource id
 */
fun TextView.setFont(@StyleRes resId: Int) {
  TextViewCompat.setTextAppearance(this, resId)
}

/**
 * Get color from color resource
 *
 * @param colorRes color resource id
 * @return color
 */
@ColorInt
fun Context.retrieveColor(@ColorRes colorRes: Int): Int {
  return ContextCompat.getColor(this, colorRes)
}

/**
 * Get drawable from drawable resource
 *
 * @param drawableRes drawable resource id
 * @return drawable
 */
fun Context.retrieveDrawable(@DrawableRes drawableRes: Int): Drawable? {
  return ContextCompat.getDrawable(this, drawableRes)
}

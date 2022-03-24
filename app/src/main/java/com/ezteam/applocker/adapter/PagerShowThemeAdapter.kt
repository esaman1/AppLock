package com.ezteam.applocker.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide

class PagerShowThemeAdapter(
    val context: Context,
    var pattern: Drawable?,
    var pin: Drawable?
) :
    PagerAdapter() {
    override fun getCount(): Int {
        return 2
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val image = AppCompatImageView(context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        image.layoutParams = params
        Glide.with(context).load(if (position==0) pattern else pin).into(image)
        container.addView(image)
        return image
    }
}
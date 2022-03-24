package com.ezteam.baseproject.extensions

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation

const val CROSS_FADE_DURATION = 350

fun ImageView.loadPhotoUrl(
    url: String,
    colorInt: Int? = null,
    colorString: String? = null,
    requestListener: RequestListener<Drawable>? = null
) {
    colorInt?.let { background = ColorDrawable(it) }
    colorString?.let { background = ColorDrawable(Color.parseColor(it)) }
    Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DURATION))
        .addListener(requestListener)
        .into(this)
        .clearOnDetach()
}

fun ImageView.loadPhotoUrlWithThumbnail(
    url: String,
    thumbnailUrl: String,
    color: String? = null,
    centerCrop: Boolean = false,
    requestListener: RequestListener<Drawable>? = null
) {
    color?.let { background = ColorDrawable(Color.parseColor(it)) }
    Glide.with(context)
        .load(url)
        .thumbnail(
            if (centerCrop) {
                Glide.with(context).load(thumbnailUrl).centerCrop()
            } else {
                Glide.with(context).load(thumbnailUrl)
            }
        )
        .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DURATION))
        .addListener(requestListener)
        .into(this)
        .clearOnDetach()
}


fun ImageView.loadBlurredImage(
    url: String,
    color: String? = null,
    requestListener: RequestListener<Drawable>? = null
) {
    color?.let { background = ColorDrawable(Color.parseColor(it)) }
    Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DURATION))
        .addListener(requestListener)
        .apply(RequestOptions.bitmapTransform(BlurTransformation()))
        .into(this)
        .clearOnDetach()
}
package com.ezteam.baseproject.extensions

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.util.Log
import android.view.View
import android.view.animation.*
import java.time.Duration
import kotlin.math.abs

fun View.loadBitmapFromView(): Bitmap {
    this.buildDrawingCache()
    val b1 = this.drawingCache
    val b = b1.copy(Bitmap.Config.ARGB_8888, false)
    this.destroyDrawingCache()
    return b
}

fun View.loadAnimation(
    animationRes: Int,
    onEnd: ((Unit) -> Unit)? = null,
    onStart: ((Unit) -> Unit)? = null,
    onRepeat: ((Unit) -> Unit)? = null,
) {
    val anim =
        AnimationUtils.loadAnimation(context, animationRes)
    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            onStart?.invoke(Unit)
        }

        override fun onAnimationEnd(animation: Animation?) {
            onEnd?.invoke(Unit)
        }

        override fun onAnimationRepeat(animation: Animation?) {
            onRepeat?.invoke(Unit)
        }
    })
    startAnimation(anim)
}

fun View.resizeViewSmooth(
    toWidth: Float,
    toHeight: Float,
    toX: Float,
    toY: Float,
    duration: Long = 500
) {
    startAnimation(ResizeAnimation(this, toWidth, toHeight, toX, toY, duration))
}

class ResizeAnimation(
    private val view: View,
    private val toWidth: Float,
    private val toHeight: Float,
    private val toX: Float,
    private val toY: Float,
    timeDuration: Long = 500
) : Animation() {

    init {
        this.duration = timeDuration
    }

    override fun applyTransformation(
        interpolatedTime: Float,
        t: Transformation?
    ) {
        val height = (toHeight - view.height) * interpolatedTime + view.height
        val width = (toWidth - view.width) * interpolatedTime + view.width
        val layoutParams = view.layoutParams
        layoutParams.height = height.toInt()
        layoutParams.width = width.toInt()
        val newY = (toY - view.y) * interpolatedTime + view.y
        val newX = (toX - view.x) * interpolatedTime + view.x
        view.x = newX
        view.y = newY
        view.requestLayout()
    }
}

fun View.hideItem() {
    alpha = 0.3f
    isEnabled = false
}

fun View.showItem() {
    alpha = 1.0f
    isEnabled = true
}

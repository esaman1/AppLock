package com.ezteam.baseproject.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

object ViewUtils {
    @JvmStatic
    fun showView(isTopView: Boolean, viewShow: View?, duration: Long) {
        if (viewShow != null && !viewShow.isShown) {
            viewShow.visibility = View.VISIBLE
            YoYo.with(if (isTopView) Techniques.SlideInDown else Techniques.SlideInUp)
                .duration(duration)
                .playOn(viewShow)
        }
    }

    @JvmStatic
    fun hideView(isTopView: Boolean, viewHide: View?, duration: Long) {
        if (viewHide != null && viewHide.isShown) {
            YoYo.with(if (isTopView) Techniques.SlideOutUp else Techniques.SlideOutDown)
                .duration(duration)
                .onEnd { viewHide.visibility = View.GONE }
                .playOn(viewHide)
        }
    }

    @JvmStatic
    fun showViewBase(techniques: Techniques?, view: View?, duration: Long, delay: Long = 0) {
        view?.let {
            if (!it.isShown) {
                view.visibility = View.VISIBLE
                YoYo.with(techniques)
                    .duration(duration)
                    .delay(delay)
                    .playOn(view)
            }
        }
    }

    @JvmStatic
    fun showViewBase(techniques: Techniques?, view: View?, duration: Long) {
        showViewBase(techniques, view, duration, 0)
    }

    @JvmStatic
    fun hideViewBase(techniques: Techniques?, view: View?, duration: Long) {
        view?.let {
            if (it.isShown) {
                YoYo.with(techniques)
                    .duration(duration)
                    .onEnd { view.visibility = View.GONE }
                    .playOn(view)
            }
        }
    }

    fun expand(v: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun rotate(view: View, from: Float, to: Float) {
        val rotate = RotateAnimation(
            from, to,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 200
        rotate.fillAfter = true
        view.animation = rotate
    }
}
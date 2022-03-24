package com.ezteam.applocker.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

object RecycleViewUtils {
    fun clearAnimation(rcl : RecyclerView){
        val animator: RecyclerView.ItemAnimator? = rcl.itemAnimator
        animator?.let {
            if (it is SimpleItemAnimator) {
                (it).supportsChangeAnimations = false
            }
        }
    }
}
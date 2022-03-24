package com.ezteam.applocker.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout

import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.LayoutPatternLockBinding
import com.ezteam.applocker.utils.ThemeUtils

class ViewPatternLock(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    lateinit var binding: LayoutPatternLockBinding

    init {
        initView()
        initListener()
    }

    private fun initListener() {
    }

    private fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_pattern_lock, this, true)
        binding = LayoutPatternLockBinding.bind(view)
        // theme
        val dotNormal = ThemeUtils.themeDotPatternNormal(context)
        val dotHighlight = ThemeUtils.themeDotPatternHighlight(context)
        val dotError = ThemeUtils.themeDotPatternError(context)
        binding.lockView.apply {
            setNodeHighlightSrc(dotHighlight)
            setNodeSrc(dotNormal)
            setNodeErrorSrc(dotError)
            setLineColor(ThemeUtils.getLineColor())
            setLineCorrectColor(ThemeUtils.getLineColor())
            setSize(3)
        }
    }
}
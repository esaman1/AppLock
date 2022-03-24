package com.ezteam.applocker.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import com.ezteam.applocker.adapter.PagerShowThemeAdapter
import com.ezteam.applocker.databinding.LayoutDialogShowThemeLockBinding

class DialogShowThemeLock(
    context: Context,
    var pattern: Drawable?,
    var pin: Drawable?,
    style: Int
) :
    AlertDialog(context, style) {
    private lateinit var binding: LayoutDialogShowThemeLockBinding
    var listenerApply: (() -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setCancelable(true)
        initData()
        initView()
        initListener()
    }

    private fun initData() {

    }

    private fun initView() {
        binding = LayoutDialogShowThemeLockBinding.inflate(LayoutInflater.from(context))
        val adapter = PagerShowThemeAdapter(context, pattern, pin)
        binding.viewPager.adapter = adapter
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)
    }

    private fun initListener() {
        binding.btnApply.setOnClickListener {
            listenerApply?.invoke()
            dismiss()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }


}
package com.ezteam.applocker.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import com.ezteam.applocker.databinding.LayoutDialogShowLockScreenBinding

class DialogShowLockScreen(context: Context, var uri: Uri?, style: Int) :
    AlertDialog(context, style) {
    private lateinit var binding: LayoutDialogShowLockScreenBinding
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
        binding = LayoutDialogShowLockScreenBinding.inflate(LayoutInflater.from(context))
        binding.img.setImageURI(uri)
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
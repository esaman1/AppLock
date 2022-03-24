package com.ezteam.applocker.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.*

class DialogHiding(context: Context) : AlertDialog(context) {
    private lateinit var binding: LayoutDialogHidingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setCancelable(false)
        initData()
        initView()
        initListener()
    }

    private fun initData() {

    }

    private fun initView() {
        binding = LayoutDialogHidingBinding.inflate(LayoutInflater.from(context))
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)
    }

    private fun initListener() {

    }
}
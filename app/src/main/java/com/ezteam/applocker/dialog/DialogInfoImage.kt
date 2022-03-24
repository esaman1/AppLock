package com.ezteam.applocker.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.LayoutDialogDeleteImageBinding
import com.ezteam.applocker.databinding.LayoutDialogInfoImageBinding
import com.ezteam.applocker.databinding.LayoutDialogQuestionBinding
import com.ezteam.applocker.databinding.LayoutDialogSelectLockBinding

class DialogInfoImage(context: Context,var binding: LayoutDialogInfoImageBinding) : AlertDialog(context) {
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
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)
    }

    private fun initListener() {
        binding.done.setOnClickListener {
            this.dismiss()
        }
    }
}
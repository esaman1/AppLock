package com.ezteam.applocker.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.LayoutDialogDeleteImageBinding
import com.ezteam.applocker.databinding.LayoutDialogQuestionBinding
import com.ezteam.applocker.databinding.LayoutDialogSelectLockBinding

class DialogDeleteImage(context: Context, var binding: LayoutDialogDeleteImageBinding) :
    AlertDialog(context) {
    var listenerNo: (() -> Unit)? = null
    var listenerYes: (() -> Unit)? = null
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
        binding.btnNo.setOnClickListener {
            listenerNo?.invoke()
        }
        binding.btnYes.setOnClickListener {
            listenerYes?.invoke()
        }

    }
}
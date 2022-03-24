package com.ezteam.baseproject.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import kotlin.collections.HashMap

abstract class BaseDialog<BD : ViewBinding, B : BuilderDialog>(var builder: B, context: Context) :
    Dialog(context) {

    protected abstract val viewBinding: BD
    lateinit var binding: BD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = viewBinding
        setContentView(binding.root)
        window?.apply {
            val windowParams = attributes
            setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            windowParams.dimAmount = 0.7f
            attributes = windowParams
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        initView()
        initListener()
    }

    override fun show() {
        super.show()
        setOnDismissListener {
            builder.dismissDialogListener?.invoke()
        }
    }

    open fun initView() {
        title?.let {
            it.text = builder.title.orEmpty()
        }

        positiveButton?.let {
            if (!builder.positiveButton.isNullOrEmpty()) {
                it.text = builder.positiveButton
                it.setOnClickListener {
                    handleClickPositiveButton(HashMap())
                }
            }
        }

        negativeButton?.let {
            if (!builder.negativeButton.isNullOrEmpty()) {
                it.text = builder.negativeButton
                it.setOnClickListener(::handleClickNegativeButton)
            }
        }

        message?.let {
            it.text = builder.message.orEmpty()
        }
    }

    protected abstract fun initListener()
    protected open val positiveButton: TextView?
        get() = null
    protected open val negativeButton: TextView?
        get() = null
    protected open val title: TextView?
        get() = null
    protected open val message: TextView?
        get() = null

    protected open fun handleClickNegativeButton(view: View) {
        builder.negativeButtonListener?.let {
            it(this)
        }
        dismiss()
    }

    protected open fun handleClickPositiveButton(data: HashMap<String?, Any?>) {
        builder.positiveButtonListener?.let {
            it(this, data)
        }
        dismiss()
    }
}
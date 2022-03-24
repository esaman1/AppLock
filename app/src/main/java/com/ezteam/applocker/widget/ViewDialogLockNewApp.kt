package com.ezteam.applocker.widget

import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.LayoutDialogLockNewAppBinding

class ViewDialogLockNewApp(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {
    lateinit var binding: LayoutDialogLockNewAppBinding

    init {
        initView()
    }

    private fun initView() {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_dialog_lock_new_app, this, true)
        binding = LayoutDialogLockNewAppBinding.bind(view)
    }
    fun setApp(pkg: String){
        try {
            val iconApp = context.packageManager.getApplicationIcon(pkg)
            val name = context.packageManager.getApplicationLabel(
                context.packageManager.getApplicationInfo(
                    pkg, PackageManager.GET_META_DATA
                )
            )
            Glide.with(context).load(iconApp).into(binding.icNewApp)
            binding.txtContentLock.text =
                context.resources.getString(R.string.content_dialog_new_app, name)

        } catch (e: PackageManager.NameNotFoundException) {
        }
    }
}
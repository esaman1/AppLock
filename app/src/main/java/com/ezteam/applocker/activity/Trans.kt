package com.ezteam.applocker.activity

import android.view.LayoutInflater
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.ActivityTransBinding
import com.ezteam.baseproject.activity.BaseActivity

class Trans : BaseActivity<ActivityTransBinding>() {
    override fun initView() {
        binding.title.text =
            resources.getString(
                R.string.find_app_permission, resources.getString(R.string.app_name)
            )
    }

    override fun initData() {
    }

    override fun initListener() {
        binding.btn.setOnClickListener {
            finish()
        }
    }

    override fun viewBinding(): ActivityTransBinding {
        return ActivityTransBinding.inflate(LayoutInflater.from(this))
    }

}
package com.ezteam.applocker.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.ActivityMainBinding
import com.ezteam.applocker.utils.AppUtil
import com.ezteam.applocker.viewmodel.AppLockViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.launchActivity
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel by inject<AppLockViewModel>()

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        Glide.with(this).load(getDrawable(R.drawable.bg_splash)).into(binding.bg)
        //
        setStatusBarHomeTransparent(this)
    }

    override fun initListener() {
        viewModel.listAppLiveData.observe(this) {
            var isConcerned = false
            for (item in it) {
                if (AppUtil.listPkgConcernedApp.contains(item.packageName)) {
                    isConcerned = true
                    break
                }
            }
            binding.btnStart.setOnClickListener {
                if (isConcerned) {
                    launchActivity<ConcernedApp> { }
                } else {
                    launchActivity<CreateLockActivity> { }
                }
                finish()
            }
        }
    }

    override fun viewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun initData() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}
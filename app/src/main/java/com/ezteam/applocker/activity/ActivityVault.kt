package com.ezteam.applocker.activity

import android.view.LayoutInflater
import com.ezteam.applocker.adapter.PagerVaultAdapter
import com.ezteam.applocker.databinding.ActivityVaultBinding
import com.ezteam.applocker.key.Vault
import com.ezteam.applocker.viewmodel.HideImageViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.ezteam.baseproject.extensions.launchActivity
import com.google.android.gms.ads.ez.EzAdControl
import org.koin.android.ext.android.inject

class ActivityVault : BaseActivity<ActivityVaultBinding>() {
    private val viewModel by inject<HideImageViewModel>()
    override fun initView() {
        // ads
        EzAdControl.getInstance(this).showAds()
        setStatusBarHomeTransparent(this)
        binding.vault.setPadding(0, getHeightStatusBar(), 0, 0)
        //
        val position = intent.getIntExtra(Vault.KEY_BACK, 0)
        val manager = supportFragmentManager
        val adapter = PagerVaultAdapter(manager, this)
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.setCurrentItem(position, true)
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.icBackVault.setOnClickListener {
            onBackPressed()
        }
        //
        binding.icAdd.setOnClickListener {
            when (binding.tabLayout.selectedTabPosition) {
                0 -> {
                    launchActivity<AlbumActivityImage> { }
                }
                1 -> {
                    launchActivity<AlbumVideoActivity> { }
                }
            }
        }
        //
        viewModel.isHide.observe(this) {
            if (it) {
                // ads
                EzAdControl.getInstance(this).showAds()
                viewModel.isHide.postValue(false)
            }
        }
    }

    override fun viewBinding(): ActivityVaultBinding {
        return ActivityVaultBinding.inflate(LayoutInflater.from(this))
    }

}
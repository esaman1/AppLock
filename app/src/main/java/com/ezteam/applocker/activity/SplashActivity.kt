package com.ezteam.applocker.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import com.ezteam.applocker.databinding.ActivitySplashBinding
import com.ezteam.applocker.item.ItemVideoHide
import com.ezteam.applocker.key.KeyApp
import com.ezteam.applocker.key.KeyLock
import com.ezteam.applocker.key.Vault
import com.ezteam.applocker.service.BackgroundManager
import com.ezteam.applocker.service.LockService
import com.ezteam.applocker.utils.NotificationLockUtils
import com.ezteam.applocker.viewmodel.AppLockViewModel
import com.ezteam.applocker.viewmodel.HideVideoModel
import com.ezteam.applocker.viewmodel.ThemeViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.launchActivity
import com.google.android.gms.ads.ez.AdFactoryListener
import com.google.android.gms.ads.ez.LogUtils
import com.google.android.gms.ads.ez.admob.AdmobOpenAdUtils
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val viewModel by inject<AppLockViewModel>()
    private val viewModelTheme by inject<ThemeViewModel>()
    private val viewModelVideo by inject<HideVideoModel>()

    @SuppressLint("UseCompatLoadingForDrawables")
    @KoinApiExtension
    override fun initView() {
        // Screen full
        setAppActivityFullScreenOver(this)
        //
        openMain()
    }

    override fun initData() {
    }

    @KoinApiExtension
    private fun openMain() {
        if (!BackgroundManager.checkCreatePassword(this)) {
            if (!BackgroundManager.checkPermission(this)) {
                Handler().postDelayed({
                    launchActivity<ActivityRequestPermission> { }
                }, 2800)
            } else {
                loadOpenAds(true)
            }
        } else {
            // show lock
            // request permission

            if (!BackgroundManager.checkPermission(this)) {
                Handler().postDelayed({
                    launchActivity<ActivityRequestPermission> { }
                }, 2800)
            } else {
                if (!BackgroundManager.isServiceRunning(LockService::class.java, this)) {
                    BackgroundManager.startService(LockService::class.java, this, this)
                }
                loadOpenAds()
            }
        }
    }

    @KoinApiExtension
    private fun loadOpenAds(isStartMain: Boolean = false) {
        AdmobOpenAdUtils.getInstance(this).setAdListener(object : AdFactoryListener() {
            override fun onError() {
                LogUtils.logString(SplashActivity::class.java, "onError")
                startAct(isStartMain)
            }

            override fun onLoaded() {
                LogUtils.logString(SplashActivity::class.java, "onLoaded")
                // show ads ngay khi loaded
                AdmobOpenAdUtils.getInstance(this@SplashActivity).showAdIfAvailable(false)
            }

            override fun onDisplay() {
                super.onDisplay()
                LogUtils.logString(SplashActivity::class.java, "onDisplay")
            }

            override fun onDisplayFaild() {
                super.onDisplayFaild()
                LogUtils.logString(SplashActivity::class.java, "onDisplayFaild")
                startAct(isStartMain)
            }

            override fun onClosed() {
                super.onClosed()
                // tam thoi bo viec load lai ads thi dismis
                LogUtils.logString(SplashActivity::class.java, "onClosed")
                startAct(isStartMain)
            }
        }).loadAd()
    }

    @KoinApiExtension
    private fun startAct(isStartMain: Boolean) {
//        val listImage = Hawk.get(Vault.KEY_FILE_NAME_IMAGE, mutableListOf<ItemImageHide>())
        val listVideo = Hawk.get(Vault.KEY_FILE_NAME_VIDEO, mutableListOf<ItemVideoHide>())
        //
        //load app
        viewModel.loadApp(this)
        viewModelTheme.getBackground(this)
        viewModelTheme.getListTheme(this)
//        if (!listImage.isNullOrEmpty()) {
//            viewModelImage.decryptListImage(listImage)
//        }
        if (!listVideo.isNullOrEmpty()) {
            viewModelVideo.decryptListVideo(listVideo, this)
        }
        Handler().postDelayed({
            if (!BackgroundManager.isServiceRunning(LockService::class.java, this)) {
                BackgroundManager.startService(LockService::class.java, this, this)
            }
            if (isStartMain) {
                launchActivity<MainActivity> { }
            } else {
                launchActivity<LockActivity> {
                    putExtra(KeyApp.LOCK_MY_APP, true)
                    putExtra(NotificationLockUtils.DETAIL, LockService.showDetailNotify)
                    putExtra(
                        KeyLock.PKG_APP, "com.ezteam.applocker"
                    )
                }
            }
            finish()
        }, 500)
    }

    override fun initListener() {
    }

    override fun viewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this))
    }

    @KoinApiExtension
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LockService.isCreate = true
    }


}
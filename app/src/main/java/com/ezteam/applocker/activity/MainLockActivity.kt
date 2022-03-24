package com.ezteam.applocker.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezteam.applocker.R
import com.ezteam.applocker.adapter.AdapterProtected
import com.ezteam.applocker.adapter.GridMainAdapter
import com.ezteam.applocker.adapter.LinerMainAdapter
import com.ezteam.applocker.databinding.ActivityMainLockBinding
import com.ezteam.applocker.item.ItemAppLock
import com.ezteam.applocker.item.ItemGridMain
import com.ezteam.applocker.item.ItemSpeedUp
import com.ezteam.applocker.key.KeyApp
import com.ezteam.applocker.key.KeyLock
import com.ezteam.applocker.service.LockService
import com.ezteam.applocker.utils.RecycleViewUtils
import com.ezteam.applocker.viewmodel.AppLockViewModel
import com.ezteam.applocker.viewmodel.HideImageViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.ezteam.baseproject.extensions.launchActivity
import com.ezteam.baseproject.utils.PreferencesUtils
import com.ezteam.baseproject.view.rate.DialogRating
import com.ezteam.baseproject.view.rate.DialogRatingState
import com.google.android.gms.ads.ez.listenner.NativeAdListener
import com.google.android.gms.ads.ez.nativead.AdmobNativeAdView2
import com.orhanobut.hawk.Hawk
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.OnTargetListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.Circle
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension

class MainLockActivity : BaseActivity<ActivityMainLockBinding>() {
    private val listGridMain = mutableListOf<ItemGridMain>()
    private val listLinerMain = mutableListOf<ItemSpeedUp>()
    private val listProtected = mutableListOf<ItemAppLock>()
    private lateinit var gridMainAdapter: GridMainAdapter
    private lateinit var linerMainAdapter: LinerMainAdapter
    private lateinit var protectedAdapter: AdapterProtected
    private val viewModel by inject<HideImageViewModel>()
    private val viewModelLock by inject<AppLockViewModel>()
    private val mi = ActivityManager.MemoryInfo()
    private lateinit var activityManager: ActivityManager
    override fun initView() {
        val px = resources.getDimensionPixelSize(R.dimen._15sdp)
        val pxs = resources.getDimensionPixelSize(R.dimen._10sdp)
        setStatusBarHomeTransparent(this)
        binding.backgroundLock.setPadding(px, pxs + getHeightStatusBar(), px, pxs)
        //
        binding.rclGid.layoutManager = object : GridLayoutManager(this, 2) {
            override fun canScrollVertically(): Boolean {
                return false
            }

            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        gridMainAdapter = GridMainAdapter(this, listGridMain)
        binding.rclGid.adapter = gridMainAdapter
        //
        binding.rclLinear.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }

            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        linerMainAdapter = LinerMainAdapter(this, listLinerMain)
        binding.rclLinear.adapter = linerMainAdapter
        //
        protectedAdapter = AdapterProtected(this, listProtected)
        binding.rclProtected.adapter = protectedAdapter
        // clear animation rcl
        RecycleViewUtils.clearAnimation(binding.rclProtected)
        activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        //
        loadAdsNative()
        // intro view
        if (PreferencesUtils.getBoolean(KeyLock.FIRST_INTRO, true)) {
            PreferencesUtils.putBoolean(KeyLock.FIRST_INTRO, false)
            binding.rclProtected.post {
                initIntroView()
            }
        }
    }

    private fun initIntroView() {
        val firstRoot = FrameLayout(this)
        val first = layoutInflater.inflate(R.layout.layout_target, firstRoot)
        val firstTarget = Target.Builder()
            .setAnchor(binding.rclProtected)
            .setShape(Circle(100f))
            .setOverlay(first)
            .setOnTargetListener(object : OnTargetListener {
                override fun onStarted() {
                }

                override fun onEnded() {
                }
            })
            .build()
        // create spotlight
        val spotlight = Spotlight.Builder(this)
            .setTargets(mutableListOf(firstTarget))
            .setBackgroundColorRes(R.color.spotlightBackground)
            .setDuration(800L)
            .setAnimation(DecelerateInterpolator(2f))
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {
                }

                override fun onEnded() {
                }
            })
            .build()

        spotlight.start()

        first.findViewById<View>(R.id.btn_got_it).setOnClickListener {
            spotlight.finish()
        }
        first.findViewById<View>(R.id.layout_target).setOnClickListener {
            // to do some thing
        }
    }

    override fun viewBinding(): ActivityMainLockBinding {
        return ActivityMainLockBinding.inflate(LayoutInflater.from(this))
    }

    @KoinApiExtension
    override fun initListener() {
        //
        binding.btnLock.setOnClickListener {
            viewModelLock.loadApp(this)
            launchActivity<AppLockActivity> { }
        }
        //
        gridMainAdapter.listenerCLick = {
            when (it) {
                getString(R.string.warning) -> {
                    launchActivity<IntrusionAlertActivity> {}
                }
                getString(R.string.setting) -> {
                    launchActivity<SettingsActivity> {}
                }
                getString(R.string.vault) -> {
                    LockService.isCreate = true // not lock when request any permission
                    requestPermission(
                        complete = { complete ->
                            if (complete) {
                                launchActivity<ActivityVault> {}
                            }
                        },
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
                getString(R.string.theme) -> {
                    launchActivity<ThemeActivity> {}
                }
            }
        }
        //
        linerMainAdapter.listenerOnClick = {
            when (listLinerMain[it].resId) {
                R.drawable.ic_data -> {
                    LockService.isCreate = true // not lock when request any permission
                    requestPermission(
                        complete = { complete ->
                            if (complete) {
                                launchActivity<CleanActivity> {}
                            }
                        },
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
                R.drawable.ic_storage -> {
                    LockService.isCreate = true // not lock when request any permission
                    requestPermission(
                        complete = { complete ->
                            if (complete) {
                                launchActivity<ActivitySearchRestore> { }
                            }
                        },
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
                R.drawable.ic_delete_same_image -> {
                    LockService.isCreate = true // not lock when request any permission
                    requestPermission(
                        complete = { complete ->
                            if (complete) {
//                                fistData = true
                                viewModel.getImagesGallery(this, false)
                                launchActivity<ActivitySameImage> { }
                            }
                        },
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
        }
        //
        binding.layoutRate.buttonRate.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("market://details?id=com.ezmobi.applock")
            startActivity(intent)
        }
    }

    private fun loadAdsNative() {
        AdmobNativeAdView2.getNativeAd(
            this,
            R.layout.native_admod_home, object : NativeAdListener() {
                override fun onError() {}
                override fun onLoaded(nativeAd: RelativeLayout?) {
                    nativeAd?.let {
                        if (it.parent!=null) {
                            (it.parent as ViewGroup).removeView(it)
                        }
                        binding.adsView.addView(it)
                    }
                }

                override fun onClickAd() {
//                TODO("Not yet implemented")
                }
            })
    }

    @KoinApiExtension
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initData() {
        LockService.isCreate = true
        //
        viewModelLock.changeAppLock.observe(this) {
            if (it) {
                getDataAppProtected()
            }
        }

        // grid
        listGridMain.apply {
            add(ItemGridMain(R.drawable.ic_theme, getString(R.string.theme), "#FDD56F"))
            add(
                ItemGridMain(
                    R.drawable.ic_warning, getString(R.string.warning), "#FF7777"
                )
            )
            add(
                ItemGridMain(
                    R.drawable.ic_locked_image, getString(R.string.vault), "#40BAFF"
                )
            )
            add(
                ItemGridMain(
                    R.drawable.ic_setting, getString(R.string.setting), "#906AFC"
                )
            )
        }
        // liner
        listLinerMain.apply {
            add(
                ItemSpeedUp(
                    R.drawable.ic_data,
                    "#39A1FF",
                    getString(R.string.memory_capacity),
                    50,
                    null,
                    (mi.totalMem - mi.availMem).toDouble(),
                    mi.totalMem.toDouble(),
                    getString(R.string.speed_up)
                )
            )
            add(
                ItemSpeedUp(
                    R.drawable.ic_storage, "#FDD56F", getString(R.string.recovery),
                    50, getString(R.string.check_restore), 0.0, 0.0,
                    getString(R.string.check_now)
                )
            )
            add(
                ItemSpeedUp(
                    R.drawable.ic_delete_same_image,
                    "#FF7777",
                    getString(R.string.delete_same_photo),
                    50,
                    getString(R.string.content_photo),
                    0.0,
                    0.0,
                    getString(R.string.check_now)
                )
            )
        }
        // protected
        getDataAppProtected()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDataAppProtected() {
        listProtected.clear()
        val listPro = Hawk.get(KeyApp.KEY_APP_LOCK, mutableListOf<String>())
        if (!listPro.isNullOrEmpty()) {
            for (it in listPro) {
                if (listProtected.size < 3) {
                    try {
                        val ic = when (it) {
                            "com.google.android.packageinstaller" -> {
                                resources.getDrawable(R.drawable.ic_protection)
                            }
                            "null" -> {
                                resources.getDrawable(R.drawable.ic_notification)
                            }
                            else -> packageManager.getApplicationIcon(it)
                        }
                        listProtected.add(ItemAppLock(ic, "", null, true, null))
                    } catch (e: PackageManager.NameNotFoundException) {

                    }
                }
            }
        }
        listProtected.add(
            ItemAppLock(
                resources.getDrawable(R.drawable.ic_add_app),
                "add",
                null,
                true,
                null
            )
        )
        binding.txtNumberApp.text = listPro.size.toString()
        protectedAdapter.notifyDataSetChanged()
    }

    override fun onRestart() {
        super.onRestart()
        listLinerMain[0].apply {
            remainingMemory = (mi.totalMem - mi.availMem).toDouble()
            totalMemory = mi.totalMem.toDouble()
        }
        linerMainAdapter.notifyItemChanged(0)
    }

    override fun onBackPressed() {
        showAppRating(true) {
            finishAffinity()
        }
    }

    private fun showAppRating(isHardShow: Boolean, complete: (Boolean) -> Unit) {
        DialogRating.ExtendBuilder(this)
            .setHardShow(isHardShow)
            .setListener { status ->
                when (status) {
                    DialogRatingState.RATE_BAD -> {
                        toast(resources.getString(R.string.thank_for_rate))
                        complete(false)
                    }
                    DialogRatingState.RATE_GOOD -> {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data =
                            Uri.parse("market://details?id=com.ezmobi.applock")
                        startActivity(intent)
                        complete(true)
                    }
                    DialogRatingState.COUNT_TIME -> complete(false)
                }
            }
            .build()
            .show()
    }
}
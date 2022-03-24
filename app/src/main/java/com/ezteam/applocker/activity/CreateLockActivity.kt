package com.ezteam.applocker.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.ActivityLockBinding
import com.ezteam.applocker.dialog.DialogSelectLock
import com.ezteam.applocker.item.ItemEnterPin
import com.ezteam.applocker.key.KeyLock
import com.ezteam.applocker.key.KeyQuestion
import com.ezteam.applocker.key.KeyTheme
import com.ezteam.applocker.service.BackgroundManager
import com.ezteam.applocker.utils.ThemeUtils
import com.ezteam.applocker.utils.VibrationUtil
import com.ezteam.applocker.viewmodel.AppLockViewModel
import com.ezteam.applocker.viewmodel.ThemeViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.ezteam.baseproject.extensions.launchActivity
import com.ezteam.baseproject.utils.PreferencesUtils
import com.google.android.gms.ads.ez.EzAdControl
import com.orhanobut.hawk.Hawk
import com.reginald.patternlockview.PatternLockView
import org.koin.android.ext.android.inject

class CreateLockActivity : BaseActivity<ActivityLockBinding>() {
    private var passCreate: PatternLockView.Password? = null
    private var listEnterLock: MutableList<ItemEnterPin> = mutableListOf()
    private var isPinAgain = false
    private val viewModel by inject<AppLockViewModel>()
    private val viewThemeModel by inject<ThemeViewModel>()
    private var styleViewLock: String? = null
    override fun initView() {
        setStatusBarHomeTransparent(this)
        val padding = resources.getDimensionPixelSize(R.dimen._15sdp)
        binding.layoutCreateLock.setPadding(padding, getHeightStatusBar(), padding, padding)
        // request permission
//        checkCreateLock() //
        checkViewLock()
    }

    override fun viewBinding(): ActivityLockBinding {
        return ActivityLockBinding.inflate(LayoutInflater.from(this))
    }

    override fun initData() {
        // theme set up background
        if (PreferencesUtils.getString(
                KeyTheme.KEY_BACKGROUND,
                "default"
            )!=KeyTheme.BG_CUSTOM
        ) {
            binding.themeBackground.setImageDrawable(ThemeUtils.themeBackground(this))
        } else {
            viewThemeModel.customBackground.observe(this) {
                Glide.with(this).load(it).apply(
                    RequestOptions().override(
                        binding.themeBackground.width,
                        binding.themeBackground.height
                    )
                ).into(binding.themeBackground)
            }
        }
    }

    override fun initListener() {
        // pattern lock
        binding.patternLock.binding.lockView.setCallBack { password ->
            if (password.list.size > 3) {
//                listenerCorrect?.invoke()
                when {
                    passCreate==null -> {
                        passCreate = password
                        resetPattern()
                        binding.textLock.setTextColor(ThemeUtils.getLineColorText())
                        binding.textLock.text = resources.getString(R.string.make_pass_again)
                        PatternLockView.CODE_PASSWORD_CORRECT
                    }
                    passCreate!=null&&password==passCreate -> {
                        binding.textLock.setTextColor(ThemeUtils.getLineColorText())
                        binding.textLock.text = resources.getString(R.string.click_create_passwork)
                        binding.btnCreate.visibility = View.VISIBLE
                        binding.patternLock.binding.lockView.showPasswordWithAnim(password.list)
                        PatternLockView.CODE_PASSWORD_CORRECT
                    }
                    passCreate!=null&&password!=passCreate -> {
                        VibrationUtil.startVibration(this)
                        binding.textLock.setTextColor(resources.getColor(R.color.color_CCAC2D2D))
                        binding.textLock.text = resources.getString(R.string.make_pass_again)
                        PatternLockView.CODE_PASSWORD_ERROR
                    }
                    else -> {
                        PatternLockView.CODE_PASSWORD_ERROR
                    }
                }

            } else {
                // password is error
                VibrationUtil.startVibration(this)
                binding.textLock.setTextColor(resources.getColor(R.color.color_CCAC2D2D))
                resetPattern()
                PatternLockView.CODE_PASSWORD_ERROR
            }
        }
        // click create
        binding.btnCreate.setOnClickListener {
            savePassLock()
        }
        //click select view lock
        binding.layoutSelectLock.setOnClickListener {
            val dialogSelectLock = DialogSelectLock(this, binding.textSelectLock.text.toString())
            dialogSelectLock.apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                show()
                listenerClickOkay = {
                    styleViewLock = it
                    checkViewLock(true)
                }
            }
        }
        // click number
        binding.pinLock.listenerEnter = {
            listEnterLock.clear()
            var size = 0
            for (item in it) {
                if (item.isEnterPin) {
                    listEnterLock.add(item)
                    size++
                }
            }
            //
            if (size > 3&&!isPinAgain) {
                binding.btnCreate.visibility = View.VISIBLE
            } else {
                binding.btnCreate.visibility = View.INVISIBLE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==BackgroundManager.REQUEST_CODE_OVERLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    BackgroundManager.startServiceAndUsageData(this)
                }
            } else {
                BackgroundManager.startServiceAndUsageData(this)
            }
        }
    }

    private fun resetPattern() {
        binding.patternLock.postDelayed(binding.patternLock.binding.lockView::reset, 400)
    }

    private fun checkViewLock(isChangeView: Boolean = false) {
        binding.btnCreate.visibility = View.INVISIBLE
        when (if (isChangeView) styleViewLock else PreferencesUtils.getString(
            KeyLock.LOCK,
            getString(R.string.pattern)
        )) {
            getString(R.string.pattern) -> {
                // them view knock
                if (binding.pinLock.isShown) {
                    passCreate = null
                    binding.btnCreate.visibility = View.INVISIBLE
                }
                binding.textSelectLock.text = getString(R.string.pattern)
                binding.icSelectLock.setImageResource(R.drawable.ic_pattern)
                binding.patternLock.visibility = View.VISIBLE
                binding.pinLock.visibility = View.INVISIBLE
                binding.textLock.text = getString(R.string.content_pattern)
            }
            getString(R.string.pin) -> {
                if (binding.patternLock.isShown) {
                    listEnterLock.clear()
                    binding.btnCreate.visibility = View.INVISIBLE
                }
                binding.textSelectLock.text = getString(R.string.pin)
                binding.icSelectLock.setImageResource(R.drawable.ic_number_lock)
                binding.pinLock.visibility = View.VISIBLE
                binding.patternLock.visibility = View.INVISIBLE
                binding.textLock.text = getString(R.string.content_create_pin)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (intent.getBooleanExtra(KeyLock.CHANGE_PASSWORD, false)) {
            launchActivity<SettingsActivity> { }
        }
    }

    private fun savePassLock() {
        when (styleViewLock ?: PreferencesUtils.getString(
            KeyLock.LOCK,
            getString(R.string.pattern)
        )) {
            getString(R.string.pattern) -> {
                passCreate?.list.let {
                    Hawk.put(KeyLock.KEY_PATTERN, it)
                }
                createSuccessful()
            }
            else -> {
                isPinAgain = true
                binding.btnCreate.visibility = View.INVISIBLE
//                binding.pinLock.clearEnterPin()
                binding.textLock.text = getString(R.string.make_pass_again)
                binding.textLock.setTextColor(ThemeUtils.getLineColorText())
                reLoadEnterAgain()
                binding.pinLock.listenerEnterComplete = {
                    if (listEnterLock.containsAll(it)) {
                        Hawk.put(KeyLock.KEY_PIN, listEnterLock)
                        createSuccessful()
                    } else {
                        VibrationUtil.startVibration(this)
                        binding.textLock.setTextColor(resources.getColor(R.color.color_CCAC2D2D))
                        binding.textLock.text = getString(R.string.pin_incorrect)
                    }
                    binding.pinLock.clearEnterPin()
                }
            }
        }
    }

    private fun createSuccessful() {
        if (!styleViewLock.isNullOrEmpty()) {
            PreferencesUtils.putString(KeyLock.LOCK, styleViewLock)
        }
        Toast.makeText(this, "Create successful !", Toast.LENGTH_SHORT).show()
        if (intent.getBooleanExtra(KeyLock.CHANGE_PASSWORD, false)) {
//             ads
            EzAdControl.getInstance(this).showAds()
            launchActivity<MainLockActivity> { }
        } else {
            if (PreferencesUtils.getString(
                    KeyQuestion.KEY_ANSWER, ""
                ).isNullOrEmpty()
            ) {
                launchActivity<CreateQuestion> { }
            } else {
                launchActivity<MainLockActivity> { }
            }
        }
    }

    private fun reLoadEnterAgain() {
        binding.pinLock.listEnterPin.clear()
        for (item in listEnterLock) {
            if (item.isEnterPin) {
                binding.pinLock.listEnterPin.add(ItemEnterPin())
            }
        }
        binding.pinLock.adapterPin.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
    }


}
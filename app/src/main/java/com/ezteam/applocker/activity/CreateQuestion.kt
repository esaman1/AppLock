package com.ezteam.applocker.activity

import android.text.TextUtils
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.ActivityCreateQuestionBinding
import com.ezteam.applocker.key.KeyQuestion
import com.ezteam.applocker.key.KeyTheme
import com.ezteam.applocker.utils.ThemeUtils
import com.ezteam.applocker.viewmodel.ThemeViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.ezteam.baseproject.extensions.launchActivity
import com.ezteam.baseproject.utils.PreferencesUtils
import com.google.android.gms.ads.ez.EzAdControl
import org.koin.android.ext.android.inject


class CreateQuestion : BaseActivity<ActivityCreateQuestionBinding>() {
    private val viewThemeModel by inject<ThemeViewModel>()

    override fun initView() {
        setStatusBarHomeTransparent(this)
        checkViewQuestion()
        binding.layoutCreateQuestion.setPadding(0, getHeightStatusBar(), 0, 0)
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
        // save
        binding.btnSave.setOnClickListener {
            if (!TextUtils.isEmpty(binding.edtAnswer.text?.trim())) {
                PreferencesUtils.putString(
                    KeyQuestion.KEY_ANSWER, binding.edtAnswer.text.toString()
                )
                launchActivity<MainLockActivity> { }
            }
        }
        // back
        binding.icBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkViewQuestion() {
        binding.txtQuestion.text = PreferencesUtils.getString(
            KeyQuestion.KEY_QUESTION, getString(R.string.recovery_code)
        )
    }

    override fun viewBinding(): ActivityCreateQuestionBinding {
        return ActivityCreateQuestionBinding.inflate(LayoutInflater.from(this))
    }

    override fun onBackPressed() {
        if (intent.getBooleanExtra(KeyQuestion.KEY_CHANGE_QUESTION, false)) {
            // ads
            EzAdControl.getInstance(this).showAds()
            super.onBackPressed()
        } else {
            finishAffinity()
        }
    }

}
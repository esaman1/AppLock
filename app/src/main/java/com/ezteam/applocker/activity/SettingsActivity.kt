package com.ezteam.applocker.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.*
import com.ezteam.applocker.R
import com.ezteam.applocker.adapter.SettingAdapter
import com.ezteam.applocker.databinding.ActivitySettingsBinding
import com.ezteam.applocker.dialog.DialogQuestion
import com.ezteam.applocker.item.ItemSetting
import com.ezteam.applocker.key.KeyLock
import com.ezteam.applocker.key.KeyQuestion
import com.ezteam.applocker.utils.AppUtil
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.ezteam.baseproject.extensions.launchActivity
import com.ezteam.baseproject.utils.KeyboardUtils
import com.ezteam.baseproject.utils.PreferencesUtils
import com.google.android.gms.ads.ez.EzAdControl
import com.tailoredapps.biometricauth.BiometricAuth

class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {
    private val listSetting = mutableListOf<ItemSetting>()
    private lateinit var settingAdapter: SettingAdapter
    private var dialogQuestion: DialogQuestion? = null
    override fun initView() {
        // ads
        EzAdControl.getInstance(this).showAds()
        //translucentStatus
        setStatusBarHomeTransparent(this)
        binding.setting.setPadding(0, getHeightStatusBar(), 0, 0)
        // lock new app
        binding.switchLockApp.isChecked = PreferencesUtils.getBoolean(KeyLock.LOCK_NEW_APP, false)
        //
        settingAdapter = SettingAdapter(this, listSetting)
        settingAdapter.listenerClick = {
            val data = listSetting[it]
            when (data.name) {
                getString(R.string.change_passwrod) -> {
                    launchActivity<LockActivity> {
                        putExtra(
                            KeyLock.CHANGE_PASSWORD, true
                        )
                        putExtra(
                            KeyLock.PKG_APP, "com.ezteam.applocker"
                        )

                    }
                }
                getString(R.string.change_question) -> {
                    showDialogQuestion()
                }
                getString(R.string.rate) -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data =
                        Uri.parse("market://details?id=com.ezmobi.applock")
                    startActivity(intent)
                }
            }
        }
        binding.rclSetting.adapter = settingAdapter
        // load ads native
        // check view fingerprint
        checkFingerprint {
            when (it) {
                AppUtil.HAVE_SUPPORT -> {
                    binding.swichFinger.isChecked =
                        PreferencesUtils.getBoolean(KeyLock.LOCK_FINGER, false)
                }
                AppUtil.NO_ANY_FINGERPRINT -> {
                    binding.swichFinger.isChecked = false
                    //The user has not enrolled any fingerprints (i.e. fingerprint authentication is not activated by the user)
                }
                AppUtil.NOT_SUPPORT -> {
                    binding.swichFinger.isChecked = false
                    //The devices does not support fingerprint authentication (i.e. has no fingerprint hardware)
                }
            }
        }
    }

    override fun initData() {
        listSetting.clear()
        listSetting.apply {
            add(
                ItemSetting(
                    R.drawable.ic_change_password, getString(R.string.change_passwrod), null
                )
            )
            add(
                ItemSetting(
                    R.drawable.ic_change_question, getString(R.string.change_question), null
                )
            )
            add(
                ItemSetting(
                    R.drawable.ic_rate, getString(R.string.rate), null
                )
            )
        }
    }

    override fun initListener() {
        binding.icBackSettings.setOnClickListener {
            onBackPressed()
        }
        //
        binding.switchLockApp.setOnCheckedChangeListener { buttonView, isChecked ->
            PreferencesUtils.putBoolean(KeyLock.LOCK_NEW_APP, isChecked)
        }
        //
        binding.swichFinger.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkFingerprint {
                    when (it) {
                        AppUtil.HAVE_SUPPORT -> {
                            PreferencesUtils.putBoolean(KeyLock.LOCK_FINGER, true)
                        }
                        AppUtil.NO_ANY_FINGERPRINT -> {
                            binding.swichFinger.isChecked = false
                            //The user has not enrolled any fingerprints (i.e. fingerprint authentication is not activated by the user)
                            toast(getString(R.string.dont_have_fingerprint))
                        }
                        AppUtil.NOT_SUPPORT -> {
                            binding.swichFinger.isChecked = false
                            //The devices does not support fingerprint authentication (i.e. has no fingerprint hardware)
                            toast(getString(R.string.can_not_use_finger_print))
                        }
                    }
                }
            } else {
                PreferencesUtils.putBoolean(KeyLock.LOCK_FINGER, false)
            }
        }
    }

    override fun viewBinding(): ActivitySettingsBinding {
        return ActivitySettingsBinding.inflate(LayoutInflater.from(this))
    }

    private fun checkFingerprint(state: (Int) -> Unit) {
        val biometricAuth = BiometricAuth.create(this, useAndroidXBiometricPrompt = false)
        if (!biometricAuth.hasFingerprintHardware) {
            state.invoke(AppUtil.NOT_SUPPORT)
//            //The devices does not support fingerprint authentication (i.e. has no fingerprint hardware)
        } else if (!biometricAuth.hasFingerprintsEnrolled) {
//            //The user has not enrolled any fingerprints (i.e. fingerprint authentication is not activated by the user)
            state.invoke(AppUtil.NO_ANY_FINGERPRINT)
        } else {
            state.invoke(AppUtil.HAVE_SUPPORT)
        }
    }

    private fun showDialogQuestion() {
        KeyboardUtils.showSoftKeyboard(this)
        dialogQuestion = DialogQuestion(this, R.style.StyleDialog)
        dialogQuestion?.setCanceledOnTouchOutside(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            dialogQuestion?.window!!.setType(
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            )
        else
            dialogQuestion?.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        dialogQuestion?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialogQuestion?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogQuestion?.show()
        dialogQuestion?.listenerClickOkay = {
            KeyboardUtils.hideSoftKeyboardToggleSoft(this)
            val answer = PreferencesUtils.getString(KeyQuestion.KEY_ANSWER, "")
            if (it.equals(answer, true)) {
                launchActivity<CreateQuestion> {
                    putExtra(KeyQuestion.KEY_CHANGE_QUESTION, true)
                }
                dialogQuestion?.dismiss()
            } else {
                dialogQuestion?.binding?.txtError?.visibility = View.VISIBLE
            }
        }
    }
}
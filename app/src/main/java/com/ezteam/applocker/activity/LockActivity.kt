package com.ezteam.applocker.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.androidhiddencamera.*
import com.androidhiddencamera.config.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.ActivityLockSettingBinding
import com.ezteam.applocker.dialog.DialogQuestion
import com.ezteam.applocker.key.*
import com.ezteam.applocker.service.LockService
import com.ezteam.applocker.utils.AppUtil
import com.ezteam.applocker.utils.NotificationLockUtils
import com.ezteam.applocker.utils.ThemeUtils
import com.ezteam.applocker.utils.VibrationUtil
import com.ezteam.applocker.viewmodel.TakePhotoViewModel
import com.ezteam.applocker.viewmodel.ThemeViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.launchActivity
import com.ezteam.baseproject.utils.KeyboardUtils
import com.ezteam.baseproject.utils.PreferencesUtils
import com.google.android.gms.ads.ez.listenner.NativeAdListener
import com.google.android.gms.ads.ez.nativead.AdmobNativeAdView2
import com.orhanobut.hawk.Hawk
import com.reginald.patternlockview.PatternLockView
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension


class LockActivity : BaseActivity<ActivityLockSettingBinding>(), CameraCallbacks {
    private var mCameraPreview: CameraPreview? = null
    private var mCachedCameraConfig: CameraConfig? = null
    private var dialogQuestion: DialogQuestion? = null
    private var isDetailNotify: Boolean = false
    private val viewModel by inject<TakePhotoViewModel>()
    private val viewThemeModel by inject<ThemeViewModel>()
    private var mCameraConfig: CameraConfig? = null
    private var pkg: String? = null

    @KoinApiExtension
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
//        //set up theme
        binding.textLock.setTextColor(ThemeUtils.getLineColorText())
//        binding.bgLock.setBackgroundDrawable(ThemeUtils.themeBackground(this))
        //
        isDetailNotify = intent.getBooleanExtra(NotificationLockUtils.DETAIL, false)
        //
        configCamera()
        //Add the camera preview surface to the root of the activity view.
        mCameraPreview = addPreView()
        //
        setAppActivityFullScreenOver(this)
        checkViewLock()
        // start camera
        //Start camera preview
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            )==PackageManager.PERMISSION_GRANTED
            &&PreferencesUtils.getBoolean(IntrusionAlert.ON_INTRUSION, false)
        ) {
            startCamera(mCameraConfig!!)
        }
        //load ads
        loadAds()
        // finger print
        if (PreferencesUtils.getBoolean(KeyLock.LOCK_FINGER, false)) {
            AppUtil.initFingerprint(this) {
                if (it) {
                    PreferencesUtils.putInteger(KeyLock.TIME_ERROR, 0)
                    PreferencesUtils.putInteger(KeyLock.TIME_QUESTION, 0)
                    passWordCorrect()
                }
            }
        }
        // load icon
        pkg = intent.getStringExtra(KeyLock.PKG_APP)
        if (!pkg.isNullOrEmpty()) {
            try {
                val iconApp = packageManager.getApplicationIcon(pkg!!)
                Glide.with(this).load(iconApp).into(binding.icIconApp)
            } catch (ex: PackageManager.NameNotFoundException) {
            }
        }
    }

    private fun loadAds() {
        AdmobNativeAdView2.getNativeAd(
            this,
            R.layout.native_admod_lock,
            object : NativeAdListener() {
                override fun onError() {
                }

                override fun onLoaded(nativeAd: RelativeLayout?) {
                    nativeAd?.let {
                        if (it.parent!=null) {
                            (it.parent as ViewGroup).removeView(it)
                        }
                        binding.adsView.addView(it)
                        binding.adsView.let { ads ->
                        }
                        binding.icIconApp.animate().alpha(0F).duration = 100
                    }
                }

                override fun onClickAd() {
                }
            })
    }

    override fun viewBinding(): ActivityLockSettingBinding {
        return ActivityLockSettingBinding.inflate(LayoutInflater.from(this))
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

    @KoinApiExtension
    override fun initListener() {
        // pattern lock
        binding.patternLock.binding.lockView.setCallBack {
            when (it.list) {
                Hawk.get(
                    KeyLock.KEY_PATTERN, mutableListOf<Int>()
                ) -> {
                    PreferencesUtils.putInteger(KeyLock.TIME_ERROR, 0)
                    PreferencesUtils.putInteger(KeyLock.TIME_QUESTION, 0)
                    passWordCorrect()
                    PatternLockView.CODE_PASSWORD_CORRECT
                }
                else -> {
                    takeAutoPhoto()
                    passWordInCorrect(getString(R.string.wrong_pattern))
                    PatternLockView.CODE_PASSWORD_ERROR
                }
            }
        }
        // pin lock
        binding.pinLock.listenerCorrect = {
            PreferencesUtils.putInteger(KeyLock.TIME_ERROR, 0)
            PreferencesUtils.putInteger(KeyLock.TIME_QUESTION, 0)
            Handler().postDelayed({
                passWordCorrect()
            }, 270)
        }
        binding.pinLock.listenerInCorrect = {
            passWordInCorrect(getString(R.string.pin_incorrect))
            takeAutoPhoto()
        }
        // knock code
    }

    @KoinApiExtension
    private fun takeAutoPhoto() {
        val time = PreferencesUtils.getInteger(KeyLock.TIME_ERROR, 0)
        val timeQuestion = PreferencesUtils.getInteger(KeyLock.TIME_QUESTION, 0)
        //error
        if (time==PreferencesUtils.getInteger(KeyLock.TIME_TAKE_PHOTO, 3) - 1) {
            if (PreferencesUtils.getBoolean(IntrusionAlert.ON_INTRUSION, false)) {
                takePicture()
            }
            PreferencesUtils.putInteger(KeyLock.TIME_ERROR, 0)
            PreferencesUtils.putInteger(KeyLock.TIME_QUESTION, 0)
        } else {
            PreferencesUtils.putInteger(KeyLock.TIME_ERROR, time + 1)
        }
        // question
        if (timeQuestion==5) {
            showDialogQuestion()
            PreferencesUtils.putInteger(KeyLock.TIME_QUESTION, 0)
        } else {
            PreferencesUtils.putInteger(KeyLock.TIME_QUESTION, timeQuestion + 1)
        }
    }

    private fun checkViewLock() {
        when (PreferencesUtils.getString(KeyLock.LOCK, getString(R.string.pattern))) {
            getString(R.string.pattern) -> {
                binding.patternLock.visibility = View.VISIBLE
                binding.pinLock.visibility = View.INVISIBLE
                binding.textLock.text = getString(R.string.content_pattern)
            }
            getString(R.string.pin) -> {
                binding.pinLock.visibility = View.VISIBLE
                binding.patternLock.visibility = View.INVISIBLE
                binding.textLock.text = getString(R.string.enter_pin)
            }
        }
    }

    private fun configCamera() {
        mCameraConfig = CameraConfig()
            .getBuilder(this)
            .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
            .setCameraResolution(CameraResolution.HIGH_RESOLUTION)
            .setImageFormat(CameraImageFormat.FORMAT_JPEG)
            .setImageRotation(CameraRotation.ROTATION_270)
            .setCameraFocus(CameraFocus.AUTO)
            .build()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        startActivity(homeIntent)
        finishAffinity()
    }


    @KoinApiExtension
    private fun passWordCorrect() {
        when {
            intent.getBooleanExtra(KeyApp.LOCK_MY_APP, false) -> {
                if (PreferencesUtils.getString(KeyQuestion.KEY_ANSWER, "").isNullOrEmpty()) {
                    launchActivity<CreateQuestion> { }
                } else {
                    if (isDetailNotify) {
                        launchActivity<DetailNotificationActivity> { }
                    } else {
                        launchActivity<MainLockActivity> { }
                    }
                }
            }
            intent.getBooleanExtra(KeyLock.CHANGE_PASSWORD, false) -> {
                launchActivity<CreateLockActivity> {
                    putExtra(KeyLock.CHANGE_PASSWORD, true)
                }
            }
            else -> {
                finishAffinity()
            }
        }
    }

    private fun passWordInCorrect(error: String) {
        VibrationUtil.startVibration(this)
        binding.textLock.setTextColor(resources.getColor(R.color.color_CCAC2D2D))
        binding.textLock.text = error
    }

    private fun addPreView(): CameraPreview {
        //create fake camera view
        val cameraSourceCameraPreview = CameraPreview(this, this)
        cameraSourceCameraPreview.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        val view = (window.decorView.rootView as ViewGroup).getChildAt(0)
        if (view is LinearLayout) {
            val params = LinearLayout.LayoutParams(1, 1)
            view.addView(cameraSourceCameraPreview, params)
        } else if (view is RelativeLayout) {
            val params = RelativeLayout.LayoutParams(1, 1)
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            view.addView(cameraSourceCameraPreview, params)
        } else if (view is FrameLayout) {
            val params = FrameLayout.LayoutParams(1, 1)
            view.addView(cameraSourceCameraPreview, params)
        } else {
            throw RuntimeException(
                "Root view of the activity/fragment cannot be other than Linear/Relative/Frame layout"
            )
        }
        return cameraSourceCameraPreview
    }

    override fun onImageCapture(bitmap: Bitmap) {
        viewModel.saveImage(bitmap, this)
    }

    override fun onCameraError(errorCode: Int) {
    }

    @KoinApiExtension
    override fun onDestroy() {
        super.onDestroy()
        stopCamera()
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            ==PackageManager.PERMISSION_GRANTED
        ) {
            mCachedCameraConfig?.let {
                startCamera(it)
            }
        }
    }

    @KoinApiExtension
    private fun stopCamera() {
        LockService.isLocking = false
        mCachedCameraConfig = null //Remove config.
        if (mCameraPreview!=null) {
            mCameraPreview!!.stopPreviewAndFreeCamera()
//            mCameraPreview = null
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    fun startCamera(cameraConfig: CameraConfig) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            !=PackageManager.PERMISSION_GRANTED
        ) { //check if the camera permission is available
            onCameraError(CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE)
        } else if (cameraConfig.facing==CameraFacing.FRONT_FACING_CAMERA
            &&!HiddenCameraUtils.isFrontCameraAvailable(this)
        ) {   //Check if for the front camera
            onCameraError(CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA)
        } else {
            mCachedCameraConfig = cameraConfig
            mCameraPreview!!.startCameraInternal(cameraConfig)
        }
    }

    private fun takePicture() {
        if (mCameraPreview!=null) {
            if (mCameraPreview!!.isSafeToTakePictureInternal) mCameraPreview!!.takePictureInternal()
        } else {
            throw java.lang.RuntimeException(
                "Background camera not initialized. Call startCamera() to initialize the camera."
            )
        }
    }

    @KoinApiExtension
    private fun showDialogQuestion() {
        KeyboardUtils.showSoftKeyboard(this)
        dialogQuestion = DialogQuestion(this, R.style.StyleDialog)
        dialogQuestion?.setCancelable(true)
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
            val answer = PreferencesUtils.getString(KeyQuestion.KEY_ANSWER, "")
            if (it.equals(answer, true)) {
                passWordCorrect()
            } else {
                passWordInCorrect(getString(R.string.answer_incorrect))
            }
            dialogQuestion?.dismiss()
            KeyboardUtils.hideSoftKeyboard(this)
        }
    }
}
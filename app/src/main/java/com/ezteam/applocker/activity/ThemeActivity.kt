package com.ezteam.applocker.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.Window
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.ActivityThemeBinding
import com.ezteam.applocker.dialog.DialogShowLockScreen
import com.ezteam.applocker.fragment.FragmentLockScreen
import com.ezteam.applocker.fragment.FragmentLockTheme
import com.ezteam.applocker.key.KeyTheme
import com.ezteam.applocker.viewmodel.ThemeViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.adapter.BasePagerAdapter
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.ezteam.baseproject.utils.PreferencesUtils
import com.google.android.gms.ads.ez.EzAdControl
import com.yalantis.ucrop.UCrop
import org.koin.android.ext.android.inject
import java.io.FileNotFoundException


class ThemeActivity : BaseActivity<ActivityThemeBinding>() {
    private var dialogShow: DialogShowLockScreen? = null
    private val viewModel by inject<ThemeViewModel>()
    private val fragmentLockScreen = FragmentLockScreen()
    override fun initView() {
        // ads
        EzAdControl.getInstance(this).showAds()
        //
        setStatusBarHomeTransparent(this)
        binding.theme.setPadding(0, getHeightStatusBar(), 0, 0)
        //

        val adapter = BasePagerAdapter(supportFragmentManager, 0).apply {
            addFragment(FragmentLockTheme(), getString(R.string.LOCK_THEME))
            addFragment(fragmentLockScreen, getString(R.string.LOCK_BACKGROUND))
        }
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.setCurrentItem(0, true)
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.icBackTheme.setOnClickListener {
            onBackPressed()
        }
    }

    override fun viewBinding(): ActivityThemeBinding {
        return ActivityThemeBinding.inflate(LayoutInflater.from(this))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==RESULT_OK&&requestCode==UCrop.REQUEST_CROP) {
            data?.let {
                val resultUri: Uri? = UCrop.getOutput(it)
                showDialogShow(resultUri)
            }

        } else if (resultCode==UCrop.RESULT_ERROR) {
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showDialogShow(uri: Uri?) {
        dialogShow = DialogShowLockScreen(this, uri, R.style.MyDialogTheme)
        dialogShow?.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            show()
            listenerApply = {
//                change adapter rcl
                fragmentLockScreen.apply {
                    for (i in listPhotoScreen) {
                        if (i.folder==KeyTheme.BG_CUSTOM) {
                            PreferencesUtils.putString(KeyTheme.KEY_BACKGROUND, i.folder)
                            i.isSelected = true
                            adapterLockScreen?.notifyItemChanged(listPhotoScreen.indexOf(i))
                        } else {
                            i.isSelected = false
                            adapterLockScreen?.notifyItemChanged(listPhotoScreen.indexOf(i))
                        }
                    }
                }
                try {
                    uri?.let {
                        val inputStream = contentResolver.openInputStream(it)
                        val dra = Drawable.createFromStream(inputStream, it.toString())
                        viewModel.customBackground.postValue(dra)
                    }
                } catch (e: FileNotFoundException) {
                    viewModel.customBackground.postValue(getDrawable(R.drawable.background_lock_app))
                }
                dialogShow?.dismiss()
                // ads
                EzAdControl.getInstance(this@ThemeActivity).showAds()
            }
        }
    }
}
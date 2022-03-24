package com.ezteam.applocker.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.ezteam.applocker.R
import com.ezteam.applocker.activity.ThemeActivity
import com.ezteam.applocker.adapter.AdapterLockScreen
import com.ezteam.applocker.databinding.LayoutFragmentLockScreenBinding
import com.ezteam.applocker.item.ItemLockScreen
import com.ezteam.applocker.key.KeyTheme
import com.ezteam.applocker.utils.RecycleViewUtils
import com.ezteam.applocker.viewmodel.ThemeViewModel
import com.ezteam.baseproject.fragment.BaseFragment
import com.ezteam.baseproject.utils.PreferencesUtils
import com.google.android.gms.ads.ez.listenner.NativeAdListener
import com.google.android.gms.ads.ez.nativead.AdmobNativeAdView2
import com.yalantis.ucrop.UCrop
import org.koin.android.ext.android.inject
import java.io.File


class FragmentLockScreen : BaseFragment<LayoutFragmentLockScreenBinding>() {
    private val PICK_IAMAGE = 1
    var listPhotoScreen = mutableListOf<ItemLockScreen>()
    var adapterLockScreen: AdapterLockScreen? = null
    private val viewModel by inject<ThemeViewModel>()

    override fun initView() {

        adapterLockScreen = AdapterLockScreen(requireContext(), listPhotoScreen)
        binding.rcl.adapter = adapterLockScreen
        (binding.rcl.layoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position==3) {
                        3
                    } else {
                        1
                    }
                }

            }
        // clear anim rcl
        RecycleViewUtils.clearAnimation(binding.rcl)
        //load ads
        loadAds()
    }

    override fun initData() {
        listPhotoScreen.clear()
        viewModel.listBackground.observe(this) {
            if (!it.isNullOrEmpty()) {
                listPhotoScreen.clear()
                listPhotoScreen.addAll(it)
                adapterLockScreen?.notifyDataSetChanged()
            } else {
                viewModel.getBackground(requireContext())
            }
        }
//        Handler().postDelayed({
//            if (listPhotoScreen.isNullOrEmpty()) {
//                viewModel.getBackground(requireContext())
//            }
//        }, 3000)
        //
        viewModel.changeFolderTheme.observe(this) {
            for (item in listPhotoScreen) {
                if (item.folder==it) {
                    item.isSelected = true
                    adapterLockScreen?.notifyItemChanged(listPhotoScreen.indexOf(item))
                } else if (item.isSelected) {
                    item.isSelected = false
                    adapterLockScreen?.notifyItemChanged(listPhotoScreen.indexOf(item))
                }
            }
        }
    }

    override fun initListener() {
        adapterLockScreen?.listenerOnClick = {
            if (listPhotoScreen[it].folder==KeyTheme.BG_CUSTOM) {
                (activity as ThemeActivity).requestPermission(complete = { allow ->
                    if (allow) {
                        pickImage()
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                for (i in listPhotoScreen) {
                    if (i.folder==listPhotoScreen[it].folder) {
                        PreferencesUtils.putString(
                            KeyTheme.KEY_BACKGROUND,
                            listPhotoScreen[it].folder
                        )
                        i.isSelected = true
                        adapterLockScreen?.notifyItemChanged(listPhotoScreen.indexOf(i))
                    } else {
                        i.isSelected = false
                        adapterLockScreen?.notifyItemChanged(listPhotoScreen.indexOf(i))
                    }
                }
            }
        }
    }

    private fun loadAds() {
        AdmobNativeAdView2.getNativeAd(
            context,
            R.layout.native_admob_item_theme,
            object : NativeAdListener() {
                override fun onError() {
                }

                override fun onLoaded(nativeAd: RelativeLayout?) {
                    adapterLockScreen?.adsView = nativeAd
                    adapterLockScreen?.addAds(
                        ItemLockScreen(
                            "", null,
                            isSelected = false,
                            isAds = true
                        ), 3
                    )
                }

                override fun onClickAd() {
                }
            })
    }

    private fun pickImage() {
        try {
            val pickPhoto =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhoto, PICK_IAMAGE)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IAMAGE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IAMAGE -> {
                if (resultCode==RESULT_OK) {
                    data?.data?.let {
                        UCrop.of(it, Uri.fromFile(File(requireContext().cacheDir, "abc.png")))
                            .useSourceImageAspectRatio()
                            .withAspectRatio(9F, 16F)
                            .start(activity as ThemeActivity)
                    }
                }
            }
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutFragmentLockScreenBinding {
        return LayoutFragmentLockScreenBinding.inflate(inflater, container, false)
    }
}
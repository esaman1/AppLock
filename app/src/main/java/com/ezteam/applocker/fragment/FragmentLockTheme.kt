package com.ezteam.applocker.fragment

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.ezteam.applocker.R
import com.ezteam.applocker.activity.ThemeActivity
import com.ezteam.applocker.adapter.AdapterLockTheme
import com.ezteam.applocker.databinding.LayoutFragmentLockThemeBinding
import com.ezteam.applocker.dialog.DialogShowThemeLock
import com.ezteam.applocker.item.ItemLockTheme
import com.ezteam.applocker.key.KeyTheme
import com.ezteam.applocker.utils.RecycleViewUtils
import com.ezteam.applocker.viewmodel.ThemeViewModel
import com.ezteam.baseproject.fragment.BaseFragment
import com.ezteam.baseproject.utils.PreferencesUtils
import com.google.android.gms.ads.ez.EzAdControl
import com.google.android.gms.ads.ez.listenner.NativeAdListener
import com.google.android.gms.ads.ez.nativead.AdmobNativeAdView2
import org.koin.android.ext.android.inject

class FragmentLockTheme : BaseFragment<LayoutFragmentLockThemeBinding>() {
    private val listLockTheme = mutableListOf<ItemLockTheme>()
    private lateinit var adapterLockTheme: AdapterLockTheme
    private var dialogShow: DialogShowThemeLock? = null
    private val viewModel by inject<ThemeViewModel>()
    override fun initView() {
        adapterLockTheme = AdapterLockTheme(requireContext(), listLockTheme)
        binding.rclLockTheme.adapter = adapterLockTheme
        (binding.rclLockTheme.layoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position==3) {
                        3
                    } else {
                        1
                    }
                }

            }
        // remove anim rcl
        RecycleViewUtils.clearAnimation(binding.rclLockTheme)
        //load ads
        loadAds()
    }

    private fun loadAds() {
        AdmobNativeAdView2.getNativeAd(
            context,
            R.layout.native_admob_item_theme,
            object : NativeAdListener() {
                override fun onError() {
                }

                override fun onLoaded(nativeAd: RelativeLayout?) {
                    adapterLockTheme.adsView = nativeAd
                    adapterLockTheme.addAds(
                        ItemLockTheme(
                            "", null, null,
                            isSelected = false,
                            isAds = true
                        ), 3
                    )
                }

                override fun onClickAd() {
                }
            })
    }

    override fun initData() {
        viewModel.listTheme.observe(this) {
            if (!it.isNullOrEmpty()) {
                listLockTheme.clear()
                listLockTheme.addAll(it)
                adapterLockTheme.notifyDataSetChanged()
            } else {
                viewModel.getListTheme(requireContext())
            }
        }
//        Handler().postDelayed({
//            if (listLockTheme.isNullOrEmpty()) {
//                viewModel.getListTheme(requireContext())
//            }
//        }, 3000)
    }

    override fun initListener() {
        adapterLockTheme.listenerOnClick = {
            val data = listLockTheme[it]
            showDialogShow(data.themPattern, data.themPin, it)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutFragmentLockThemeBinding {
        return LayoutFragmentLockThemeBinding.inflate(inflater, container, false)
    }

    private fun showDialogShow(pattern: Drawable?, pin: Drawable?, position: Int) {
        dialogShow = DialogShowThemeLock(requireContext(), pattern, pin, R.style.MyDialogTheme)
        dialogShow?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogShow?.show()
        dialogShow?.listenerApply = {
            for (item in listLockTheme) {
                if (item.folder==listLockTheme[position].folder) {
                    PreferencesUtils.putString(KeyTheme.KEY_THEME, item.folder)
                    PreferencesUtils.putString(KeyTheme.KEY_BACKGROUND, item.folder)
                    item.isSelected = true
                    adapterLockTheme.notifyItemChanged(listLockTheme.indexOf(item))
                    viewModel.changeFolderTheme.value = item.folder
                } else if (item.isSelected) {
                    item.isSelected = false
                    adapterLockTheme.notifyItemChanged(listLockTheme.indexOf(item))
                }
            }
            // ads
            EzAdControl.getInstance(activity as ThemeActivity).showAds()
        }
    }
}
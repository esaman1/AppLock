package com.ezteam.applocker.activity

import android.view.LayoutInflater
import com.ezteam.applocker.adapter.AdapterConcernedApp
import com.ezteam.applocker.databinding.ActivityConcernedAppBinding
import com.ezteam.applocker.item.ItemAppLock
import com.ezteam.applocker.key.KeyApp
import com.ezteam.applocker.utils.AppUtil
import com.ezteam.applocker.utils.RecycleViewUtils
import com.ezteam.applocker.viewmodel.AppLockViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.ezteam.baseproject.extensions.launchActivity
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject

class ConcernedApp : BaseActivity<ActivityConcernedAppBinding>() {
    private val viewModel by inject<AppLockViewModel>()
    private var listApp: MutableList<ItemAppLock> = mutableListOf()
    private var adapterConcernedApp: AdapterConcernedApp? = null
    override fun initView() {
        setStatusBarHomeTransparent(this)
        binding.title.setPadding(0, getHeightStatusBar(), 0, 0)
        //rcl
        adapterConcernedApp = AdapterConcernedApp(this, listApp)
        binding.rcl.adapter = adapterConcernedApp
        //clear anim
        RecycleViewUtils.clearAnimation(binding.rcl)
    }

    override fun initData() {
        viewModel.listAppLiveData.observe(this) {
            listApp.clear()
            for (item in it) {
                if (AppUtil.listPkgConcernedApp.contains(item.packageName)) {
                    listApp.add(item)
                }
            }
            adapterConcernedApp?.notifyDataSetChanged()
        }
    }

    override fun initListener() {
        adapterConcernedApp?.onClickListener = {
            listApp[it].isLocked = !listApp[it].isLocked
            adapterConcernedApp?.notifyItemChanged(it)
        }
        binding.btnProtected.setOnClickListener {
            launchActivity<CreateLockActivity> { }
            val listLock = Hawk.get(KeyApp.KEY_APP_LOCK, mutableListOf<String>())
            for (item in listApp) {
                if (item.isLocked) {
                    if (!listLock.contains(item.packageName)) {
                        item.packageName?.let {
                            listLock.add(it)
                        }
                    }
                }
            }
            Hawk.put(KeyApp.KEY_APP_LOCK, listLock)
        }
    }

    override fun viewBinding(): ActivityConcernedAppBinding {
        return ActivityConcernedAppBinding.inflate(LayoutInflater.from(this))
    }

}
package com.ezteam.applocker.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import com.ezteam.applocker.R
import com.ezteam.applocker.adapter.AppLockAdapter
import com.ezteam.applocker.databinding.ActivityAppLockBinding
import com.ezteam.applocker.item.ItemAppLock
import com.ezteam.applocker.key.KeyApp
import com.ezteam.applocker.service.LockService.Companion.listMyApp
import com.ezteam.applocker.viewmodel.AppLockViewModel
import com.ezteam.applocker.viewmodel.NotifyViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.ezteam.baseproject.utils.PreferencesUtils
import com.google.android.gms.ads.ez.EzAdControl
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension

class AppLockActivity : BaseActivity<ActivityAppLockBinding>() {
    companion object {
        var isNotifyPermission = false
    }

    private var listApp: MutableList<ItemAppLock> = mutableListOf()
    private var listSearch: MutableList<ItemAppLock> = mutableListOf()
    private var adapterApp: AppLockAdapter? = null
    private val viewModel by inject<AppLockViewModel>()
    private val viewNotifyModel by inject<NotifyViewModel>()
    private val viewModelLock by inject<AppLockViewModel>()

    override fun initView() {
        // ads
        EzAdControl.getInstance(this).showAds()
        //
        //translucentStatus
        setStatusBarHomeTransparent(this)
        binding.applock.setPadding(0, getHeightStatusBar(), 0, 0)
        adapterApp = AppLockAdapter(this, listApp)
        binding.rclApp.adapter = adapterApp
    }

    @KoinApiExtension
    override fun initData() {
        isNotifyPermission = false
        // show app
        viewModel.listAppLiveData.observe(this) {
            addDataApp(it.toMutableList())
        }
//        viewModel.loadApp(this)
    }

    override fun viewBinding(): ActivityAppLockBinding {
        return ActivityAppLockBinding.inflate(LayoutInflater.from(this))
    }

    override fun initListener() {
        adapterApp?.listenerClickItem = {
            if (it >= 0) {
                if (listApp[it].packageName=="null") {
                    if (checkPermission()) {
                        viewNotifyModel.isLockNotification.postValue(!listApp[it].isLocked)
                        PreferencesUtils.putBoolean(
                            KeyApp.LOCK_NOTIFICATION, !listApp[it].isLocked
                        )
                    }
                } else {
                    if (listApp[it].packageName=="com.google.android.packageinstaller") {
                        PreferencesUtils.putBoolean(
                            KeyApp.LOCK_UNINSTALL, !listApp[it].isLocked
                        )
                    }
                }
                listApp[it].isLocked = !listApp[it].isLocked
                adapterApp?.notifyItemChanged(it)
                // view model update change UI app main lock
                viewModel.changeAppLock.value = (true)
                changeListAppLocked()
            }
        }
        // back
        binding.icBackLockApp.setOnClickListener {
            onBackPressed()
        }
        // search
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                listApp.clear()
                if (s.toString()!="") {
                    for (item in listSearch) {
                        if (item.name.contains(s.toString(), true)) {
                            listApp.add(item)
                        }
                    }
                    adapterApp?.notifyDataSetChanged()
                } else {
                    listApp.addAll(listSearch)
                    adapterApp?.notifyDataSetChanged()
                }
            }

        })
    }

    private fun changeListAppLocked() {
        val lisAppLock = mutableListOf<String>() //
        for (itemAdvanced in listApp) {
            if (itemAdvanced.isLocked) {
                itemAdvanced.packageName?.let {
                    lisAppLock.add(it)
                }
            }
        } //
        Hawk.put(KeyApp.KEY_APP_LOCK, lisAppLock)
    }

    override fun onRestart() {
        super.onRestart()
        isNotifyPermission = false
    }

    private fun isNotificationServiceRunning(context: Context): Boolean {
        val contentResolver: ContentResolver = context.contentResolver
        val enabledNotificationListeners: String? =
            Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        val packageName: String = context.packageName
        return if (enabledNotificationListeners.isNullOrEmpty()) false else enabledNotificationListeners.contains(
            packageName
        )
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (!isNotificationServiceRunning(this)) {
                isNotifyPermission = true
                showExplanation(
                    getString(R.string.app_name),
                    getString(R.string.DESCRIBE_ACCESS_NOTIFICATION_POLICY), this
                )
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addDataApp(listAppLive: MutableList<ItemAppLock>) {
        listApp.clear()
        listSearch.clear()
        val listNormalUnLock = mutableListOf<ItemAppLock>()
        val listNormalLock = mutableListOf<ItemAppLock>()
        val listAdvancedUnLock = mutableListOf<ItemAppLock>()
        val listAdvancedLock = mutableListOf<ItemAppLock>()
        if (PreferencesUtils.getBoolean(KeyApp.LOCK_NOTIFICATION)) {
            addItemNotification(listAdvancedLock)
        } else {
            addItemNotification(listAdvancedUnLock)
        }
        if (PreferencesUtils.getBoolean(KeyApp.LOCK_UNINSTALL)) {
            addItemUninstall(listAdvancedLock)
        } else {
            addItemUninstall(listAdvancedUnLock)
        }
        for (itemApp in listAppLive) {
            if (itemApp.packageName=="com.android.settings"&&itemApp.isLocked||
                itemApp.packageName=="com.android.vending"&&itemApp.isLocked
            ) {
                if (itemApp.name.isNotEmpty()) {
                    listAdvancedLock.add(itemApp)
                }
            } else if (itemApp.isLocked) {
                if (itemApp.name.isNotEmpty()) {
                    listNormalLock.add(itemApp)
                }
            }

        }
        //
        for (itemApp in listAppLive) {
            if (itemApp.packageName=="com.android.settings"&&!itemApp.isLocked||
                itemApp.packageName=="com.android.vending"&&!itemApp.isLocked
            ) {
                if (itemApp.name.isNotEmpty()) {
                    listAdvancedUnLock.add(itemApp)
                }
            } else if (!itemApp.isLocked) {
                if (itemApp.name.isNotEmpty()) {
                    listNormalUnLock.add(itemApp)
                }
            }
        }
        //sort
        listAdvancedLock.sortWith { o1, o2 ->
            o1.name.toCharArray()[0].compareTo(o2.name.toCharArray()[0])
        }
        listAdvancedUnLock.sortWith { o1, o2 ->
            o1.name.toCharArray()[0].compareTo(o2.name.toCharArray()[0])
        }
        listNormalLock.sortWith { o1, o2 ->
            o1.name.toCharArray()[0].compareTo(o2.name.toCharArray()[0])
        }
        listNormalUnLock.sortWith(compareBy { it.name })
        listNormalUnLock.sortWith(compareBy { -it.isSys })

        listApp.apply {
            add(ItemAppLock(null, getString(R.string.advanced), null, false, null))
            addAll(listAdvancedLock)
            addAll(listAdvancedUnLock)
            add(ItemAppLock(null, getString(R.string.locked), null, false, null))
            addAll(listNormalLock)
            addAll(listNormalUnLock)
        }
        listSearch.addAll(listApp)
        adapterApp?.notifyDataSetChanged()
        changeListAppLocked()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showExplanation(title: String, message: String, context: Context) {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.allow) { _, _ ->
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
            .create()
        alertDialog.show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addItemNotification(list: MutableList<ItemAppLock>) {
        list.add(
            ItemAppLock(
                resources.getDrawable(R.drawable.ic_notification),
                getString(R.string.notification_lock),
                getString(R.string.description_lock_noti),
                PreferencesUtils.getBoolean(KeyApp.LOCK_NOTIFICATION), "null"
            )
        )
        //
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addItemUninstall(list: MutableList<ItemAppLock>) {
        list.add(
            ItemAppLock(
                resources.getDrawable(R.drawable.ic_protection),
                getString(R.string.protection_lock),
                getString(R.string.description_protection_lock),
                PreferencesUtils.getBoolean(KeyApp.LOCK_UNINSTALL),
                "com.google.android.packageinstaller"
            )
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // ads
        EzAdControl.getInstance(this).showAds()
        viewModelLock.changeAppLock.value = true
    }

}
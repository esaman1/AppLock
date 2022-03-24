package com.ezteam.applocker.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezteam.applocker.R
import com.ezteam.applocker.item.ItemAppLock
import com.ezteam.applocker.key.KeyApp
import com.ezteam.applocker.service.LockService
import com.ezteam.applocker.utils.AppUtil
import com.ezteam.baseproject.viewmodel.BaseViewModel
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension

class AppLockViewModel(application: Application) : BaseViewModel(application) {
    val listAppLiveData: MutableLiveData<MutableList<ItemAppLock>> = MutableLiveData()
    var changeAppLock: MutableLiveData<Boolean> = MutableLiveData()

    @KoinApiExtension
    fun loadApp(context: Context, pkgNew: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val pkgAppsList: List<ResolveInfo> =
                context.packageManager.queryIntentActivities(mainIntent, 0)
            getItemDetailApp(pkgAppsList, context, pkgNew)
        }
    }


    @KoinApiExtension
    private fun getItemDetailApp(
        pkgAppsList: List<ResolveInfo>,
        context: Context,
        pkgNew: String? = null
    ) {
        //
        val listApp = mutableListOf<ItemAppLock>()
//        AppLok.listApp.clear()
        val listAppLocked = Hawk.get(KeyApp.KEY_APP_LOCK, mutableListOf<String>())

        for (resolveInfo in pkgAppsList) {
            // get packageName
            try {
                val pkg = resolveInfo.activityInfo.packageName
                // check pkg system
                var isSys = false
                if (isSystemPackage(resolveInfo.activityInfo.applicationInfo)) {
                    Log.d("getItemDetailApp", "getItemDetailApp: $pkg ")
                    for (it in AppUtil.listPkgSettingFinal) {
                        if (pkg.contains(it)) {
                            isSys = true
                            break
                        }
                    }
                    if (!isSys) {
                        continue
                    }
                }
//                Log.e("isSystemPackage", .toString())
                // get icon

                val iconApp = context.packageManager.getApplicationIcon(pkg)
                // get name
                val name = context.packageManager.getApplicationLabel(
                    context.packageManager.getApplicationInfo(
                        pkg, PackageManager.GET_META_DATA
                    )
                )
                // check app locked
                var isLocked = false
                for (itemLocked in listAppLocked) {
                    if (itemLocked==pkg) {
                        isLocked = true
                    }
                }
                // check lock new app
                pkgNew?.let {
                    if (pkg==it) {
                        isLocked = true
                        listAppLocked.add(it)
                        Hawk.put(KeyApp.KEY_APP_LOCK, listAppLocked)
                    }
                }
                //add
                listApp.add(
                    ItemAppLock(
                        iconApp,
                        name.toString(),
                        context.getString(R.string.application),
                        isLocked,
                        pkg, if (isSys) 1 else 0
                    )
                )
            } catch (ex: PackageManager.NameNotFoundException) {
            }
        }
        viewModelScope.launch(Dispatchers.Main) {
            listAppLiveData.value = (listApp)
            LockService.listMyApp.clear()
            LockService.listMyApp.addAll(listApp)
            pkgNew?.let {
                changeAppLock.value = true
            }
        }
    }

    private fun isSystemPackage(applicationInfo: ApplicationInfo): Boolean {
        if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM!=0) {
            return true
        }
        return false
    }
}
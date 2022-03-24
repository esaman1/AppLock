package com.ezteam.applocker.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezteam.applocker.R
import com.ezteam.applocker.item.ItemLockScreen
import com.ezteam.applocker.item.ItemLockTheme
import com.ezteam.applocker.key.KeyTheme
import com.ezteam.applocker.utils.ThemeUtils
import com.ezteam.baseproject.utils.PreferencesUtils
import com.ezteam.baseproject.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : BaseViewModel(application) {
    val customBackground: MutableLiveData<Drawable> = MutableLiveData()
    val listTheme: MutableLiveData<MutableList<ItemLockTheme>> = MutableLiveData()
    val listBackground: MutableLiveData<MutableList<ItemLockScreen>> = MutableLiveData()
    val changeFolderTheme: MutableLiveData<String> = MutableLiveData()
    fun getListTheme(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = mutableListOf<ItemLockTheme>()
            val listFolderName = ThemeUtils.getFolderName(context)
            for (folder in listFolderName) {
                context.assets.list("theme/${folder}")?.let {
                    if (it.toMutableList().size > 1) {
                        list.add(
                            ItemLockTheme(
                                folder,
                                ThemeUtils.getImageTheme(context, folder, "launcher_1"),
                                ThemeUtils.getImageTheme(context, folder, "launcher_2"),
                                checkTheme(folder)
                            )
                        )
                    }
                }
            }
//            viewModelScope.launch(Dispatchers.Main) {
            listTheme.postValue(list)
//            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getBackground(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val listPhotoScreen = mutableListOf<ItemLockScreen>()
            listPhotoScreen.add(
                ItemLockScreen(
                    KeyTheme.BG_CUSTOM,
                    context.getDrawable(R.drawable.custom_bg),
                    checkSelected(KeyTheme.BG_CUSTOM)
                )
            )
            for (folder in ThemeUtils.getFolderName(context)) {
                listPhotoScreen.add(
                    ItemLockScreen(
                        folder,
                        ThemeUtils.getImageTheme(context, folder, "background"),
                        checkSelected(folder)
                    )
                )
            }
//            viewModelScope.launch(Dispatchers.Main) {
            listBackground.postValue(listPhotoScreen)
//            }
        }
    }

    private fun checkSelected(key: String): Boolean {
        val bg = PreferencesUtils.getString(KeyTheme.KEY_BACKGROUND, "default")
        return key==bg
    }

    private fun checkTheme(key: String): Boolean {
        val save = PreferencesUtils.getString(KeyTheme.KEY_THEME, "default")
        return key==save
    }
}
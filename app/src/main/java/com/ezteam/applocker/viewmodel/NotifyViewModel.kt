package com.ezteam.applocker.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.ezteam.applocker.model.ItemNotificationModel
import com.ezteam.baseproject.viewmodel.BaseViewModel

class NotifyViewModel(application: Application) : BaseViewModel(application) {
        val listNotifyLiveData  : MutableLiveData<ItemNotificationModel> = MutableLiveData()
        val isLockNotification  : MutableLiveData<Boolean> = MutableLiveData()
}
package com.ezteam.baseproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    var isLoading = MutableLiveData(false)

    override fun onCleared() {
        super.onCleared()
    }
}
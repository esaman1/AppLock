package com.ezteam.applocker.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezteam.applocker.utils.CompareFile
import com.ezteam.baseproject.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SameImageViewModel(application: Application) : BaseViewModel(application) {
    var listSameImageLiveData: MutableLiveData<MutableList<File>?> = MutableLiveData()

    fun compareListImage(list: MutableList<File>) {
        viewModelScope.launch(Dispatchers.IO) {
            contentEquals(list)
        }
    }

    private fun contentEquals(listAllImages: MutableList<File>) {
        val listAllImage = listAllImages.sortedWith(compareBy { it.length() }).toMutableList()
        val listSetImage = mutableListOf<File>()
        var isSameLastImage = false
        var isCheckEmpty = true
        for (i in 0 until listAllImage.size) {
            isSameLastImage =
                if (i!=listAllImage.size - 1&&CompareFile.imagesAreEqual(
                        listAllImage[i],
                        listAllImage[i + 1]
                    )
                ) {
                    listSetImage.add(listAllImage[i])
                    true
                } else {
                    if (isSameLastImage) {
                        listSetImage.add(listAllImage[i])
                    }
                    if (listSetImage.isNotEmpty()) {
                        isCheckEmpty = false
                        val list = mutableListOf<File>()
                        list.addAll(listSetImage.toMutableList())
                        viewModelScope.launch(Dispatchers.Main) {
                            listSameImageLiveData.value = list
                        }
                        listSetImage.clear()
                    }
                    false
                }
        }
        if (isCheckEmpty) {
            viewModelScope.launch(Dispatchers.Main) {
                listSameImageLiveData.value = mutableListOf()
            }
        }
    }
}
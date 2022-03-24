package com.ezteam.applocker.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezteam.applocker.item.AutoPhoto
import com.ezteam.applocker.item.PhotoImage
import com.ezteam.applocker.key.IntrusionAlert
import com.ezteam.applocker.utils.ImageSaverUtils
import com.ezteam.baseproject.viewmodel.BaseViewModel
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TakePhotoViewModel(application: Application) : BaseViewModel(application) {
    var photoLiveData: MutableLiveData<PhotoImage> = MutableLiveData()


    @SuppressLint("StaticFieldLeak")
    fun takePhotoFromActivity(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = Hawk.get(IntrusionAlert.BITMAP, mutableListOf<AutoPhoto>())
            list.forEach {
                val imageSaver = ImageSaverUtils(context)
                imageSaver.fileName = it.fileName
                imageSaver.directoryName = it.directoryName
//                photoLiveData.postValue(PhotoImage(imageSaver.load(), it.time))
                viewModelScope.launch(Dispatchers.Main) {
                    photoLiveData.value = (PhotoImage(imageSaver.load(), it.time))
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun saveImage(bitmap: Bitmap, context: Context) {
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        val imageSaver = ImageSaverUtils(context)
        imageSaver.directoryName = "AutoPhoto"
        imageSaver.fileName = "${bitmap}.jpg"
        imageSaver.save(bitmap)
        val listPhoto = Hawk.get(IntrusionAlert.BITMAP, mutableListOf<AutoPhoto>())
        listPhoto.add(
            AutoPhoto(
                imageSaver.directoryName, imageSaver.fileName,
                simpleDateFormat.format(calendar.time)
            )
        )
        //
        photoLiveData.postValue(
            PhotoImage(
                imageSaver.load(),
                simpleDateFormat.format(calendar.time)
            )
        )
        Hawk.put(IntrusionAlert.BITMAP, listPhoto)
    }


    fun delete(context: Context) {
        val list = Hawk.get(IntrusionAlert.BITMAP, mutableListOf<AutoPhoto>())
        for (i in (list.size - 1 downTo 0)) {
            val directory: File = context.getDir(list[i].directoryName, Context.MODE_PRIVATE)
            if (!directory.exists()&&!directory.mkdirs()) {
            }
            val file = File(directory, list[i].fileName)
            file.delete()
        }
        Hawk.put(IntrusionAlert.BITMAP, mutableListOf<AutoPhoto>())
        photoLiveData.postValue(PhotoImage(null, ""))
    }
}

package com.ezteam.applocker.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezteam.applocker.item.ItemAlbumRestoreImages
import com.ezteam.applocker.item.ItemImageRestore
import com.ezteam.applocker.utils.Config
import com.ezteam.applocker.utils.FileUtils
import com.ezteam.baseproject.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class RestoreImageViewModel(application: Application) : BaseViewModel(application) {
    private val listPhoto = mutableListOf<ItemImageRestore>()

    //    private val listAlbumRestorePhoto = mutableListOf<ItemAlbumRestoreImages>()
    val position: MutableLiveData<Int> = MutableLiveData()
    var albumRestorePhotoLiveData: MutableLiveData<ItemAlbumRestoreImages?> = MutableLiveData()
    var listAlbumRestorePhotoLiveData: MutableLiveData<MutableList<ItemAlbumRestoreImages>?> =
        MutableLiveData()

    fun getImageRestore(context: Context) {
//        listAlbumRestorePhoto.clear()
        listPhoto.clear()
        val strArr = Environment.getExternalStorageDirectory().absolutePath
        viewModelScope.launch(Dispatchers.IO) {
            getSdCardImage(context)
            val arr = File(strArr).listFiles()
            if (!arr.isNullOrEmpty()) {
                checkFileOfDirectoryImage(strArr, arr)
            }
//            listAlbumRestorePhoto.sortWith { a, b -> if (a.lastModified > b.lastModified) 1 else if (a.lastModified < b.lastModified) -1 else 0 }
//            viewModelScope.launch(Dispatchers.Main) {
//                listAlbumRestorePhotoLiveData.value = listAlbumRestorePhoto
//            }
        }
    }

    private fun getSdCardImage(context: Context) {
        val externalStoragePaths = FileUtils.getExternalStorageDirectories(context)
        if (externalStoragePaths.size > 0) {
            for (path in externalStoragePaths) {
                val file = File(path)
                if (file.exists()) {
                    val subFiles = file.listFiles()
                    if (!subFiles.isNullOrEmpty()) {
                        checkFileOfDirectoryImage(path, subFiles)
                    }
                }
            }
        }
    }

    private fun checkFileOfDirectoryImage(temp: String, fileArr: Array<File>) {
        for (i in fileArr.indices) {
            if (fileArr[i].isDirectory) {
                val temp_sub = fileArr[i].path
                val mfileArr = File(fileArr[i].path).listFiles()
                if (!mfileArr.isNullOrEmpty()) {
                    checkFileOfDirectoryImage(temp_sub, mfileArr)
                }
            } else {
                val options: BitmapFactory.Options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(fileArr[i].path, options)
                if (!(options.outWidth==-1||options.outHeight==-1)) {
                    if (fileArr[i].path.endsWith(".jpg")
                        ||fileArr[i].path.endsWith(".jpeg")
                        ||fileArr[i].path.endsWith(".png")
                        ||fileArr[i].path.endsWith(".gif")
                    ) {
                        val file = File(fileArr[i].path)
                        val file_size = file.length()
                        if (file_size > 10000) {
                            try {
                                listPhoto.add(
                                    ItemImageRestore(
                                        fileArr[i].path,
                                        file.lastModified(),
                                        file_size
                                    )
                                )
                            } catch (e: ArrayIndexOutOfBoundsException) {
                            }
                        }
                    } else {
                        val file = File(fileArr[i].path)
                        val fileSize = file.length()
                        if (fileSize > 50000) {
                            try {
                                listPhoto.add(
                                    ItemImageRestore(
                                        fileArr[i].path,
                                        file.lastModified(),
                                        fileSize
                                    )
                                )
                            } catch (e: ArrayIndexOutOfBoundsException) {
                            }
                        }
                    }
                }
            }
        }

        if (listPhoto.isNotEmpty()&&!temp.contains(FileUtils.getPathSave(Config.RESTORE_IMAGE))) {
            val listImage = listPhoto.toMutableList()
//            listImage.sortWith { a, b ->
//                if (a.lastModified > b.lastModified) 1 else if (a.lastModified < b.lastModified) -1 else 0
//            }
            viewModelScope.launch(Dispatchers.Main) {
                albumRestorePhotoLiveData.value = ItemAlbumRestoreImages(
                    temp,
                    File(temp).lastModified(),
                    listImage
                )
            }
            listPhoto.clear()
        }
    }
}
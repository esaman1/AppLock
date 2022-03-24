package com.ezteam.applocker.viewmodel

import android.app.Application
import android.app.RecoverableSecurityException
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezteam.applocker.item.ItemDetailPhoto
import com.ezteam.applocker.item.ItemImageHide
import com.ezteam.applocker.key.Vault
import com.ezteam.applocker.utils.CryptoUtil
import com.ezteam.applocker.utils.IOUtil
import com.ezteam.applocker.utils.ImageUtil
import com.ezteam.baseproject.extensions.getFileSize
import com.ezteam.baseproject.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class HideImageViewModel(application: Application) : BaseViewModel(application) {
    val isHide: MutableLiveData<Boolean> = MutableLiveData()
    val imageHideSelected: MutableLiveData<MutableList<ItemImageHide>?> = MutableLiveData()
    val listImageHide: MutableLiveData<ItemImageHide?> = MutableLiveData()
    val recoverImage: MutableLiveData<ItemImageHide> = MutableLiveData()
    var imageStoreLiveData: MutableLiveData<File> = MutableLiveData()
    var listImageStoreLiveData: MutableLiveData<MutableList<File>?> = MutableLiveData()
    var isDeleteSameImage: MutableLiveData<Boolean> = MutableLiveData()
    fun getImagesGallery(context: Context, isAlbum: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = mutableListOf<File>()
            val uri = MediaStore.Images.Media.DATA
            // if GetImageFromThisDirectory is the name of the directory from which image will be retrieved
            val projection = arrayOf(
                uri, MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE
            )
            try {
                val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    null, null, MediaStore.Images.Media.DATE_ADDED + " DESC"
                )
                if (cursor!=null) {
                    val isDataPresent = cursor.moveToFirst()
                    if (isDataPresent) {
                        do {
                            val file = File(cursor.getString(cursor.getColumnIndex(uri)))
                            if (file.length() > 10000) {
                                viewModelScope.launch(Dispatchers.Main) {
                                    if (isAlbum) {
                                        imageStoreLiveData.value = (file)
                                    }
                                }
                                list.add(file)
                            }
                        } while (cursor.moveToNext())
                        cursor.close()
                        // list
                        if (!isAlbum) {
                            listImageStoreLiveData.postValue((list))
                        }
                    }

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    fun encryptFile(listDetailPhoto: MutableList<ItemDetailPhoto>, context: Context, path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = mutableListOf<ItemImageHide>()
            val listDetailPhotoIterator = listDetailPhoto.toMutableList()
            for (it in listDetailPhotoIterator) {
                if (it.isSelected) {
                    val file = it.file
                    ImageUtil.encryptFile(
                        context,
                        file.toUri(),
                        file.name,
                        file.getFileSize().toInt(), true, this
                    )
                    val hide = ItemImageHide(file.name, "${path}//${file.name}")
                    list.add(hide)
                }
            }
            // list
            val listHide = mutableListOf<ItemImageHide>()
            for (it in list) {
                val bitmap = loadThumbnail(it.thumbnailPath)
                it.bitmap = bitmap
                listHide.add(it)
            }
            //
            viewModelScope.launch(Dispatchers.Main) {
                isHide.value = true
                imageHideSelected.value = listHide
            }
        }
    }

    fun decryptListImage(list: MutableList<ItemImageHide>) {
        viewModelScope.launch(Dispatchers.IO) {
//            val listBitmap = mutableListOf<ItemImageHide>()
            list.forEach {
                val bitmap = loadThumbnail(it.thumbnailPath)
                it.bitmap = bitmap
//                listBitmap.add(it)
                listImageHide.postValue(it)
            }
        }
    }

    private fun loadThumbnail(path: String): Bitmap? {
        try {
            val decrypted: ByteArray? =
                CryptoUtil.decrypt(IOUtil.read(File(path)), Vault.PW(), path)
            decrypted?.let {
                return ImageUtil.decodeBitmap(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun recoverImage(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(path)
            if (file.exists()) {
                (file.delete())
            }
        }
    }

    fun deleteImage(path: String, activity: AppCompatActivity) {
        viewModelScope.launch(Dispatchers.IO) {
            //        val index = binding.viewPager.currentItem
            MediaScannerConnection.scanFile(
                activity, arrayOf(path), null
            ) { _, uri ->
                try {
                    uri?.let {
                        if (activity.contentResolver.delete(uri, null, null)!=-1) {
                        } else {
                        }
                    }
                } catch (exception: SecurityException) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (exception is RecoverableSecurityException) {
                            val editPendingIntent =
                                MediaStore.createDeleteRequest(activity.contentResolver,
                                    arrayOf(uri).map { it })
                            activity.startIntentSenderForResult(
                                editPendingIntent.intentSender, Vault.REQUEST_CODE_DELETE_IMAGE,
                                null,
                                0,
                                0,
                                0,
                                null
                            )
                        }
                    }
                }
            }
        }
    }

    fun deleteListImage(listPath: MutableList<String>, activity: AppCompatActivity) {
        viewModelScope.launch(Dispatchers.IO) {
            listPath.forEach { path ->
                deleteImage(path, activity)
            }
            viewModelScope.launch(Dispatchers.Main) {
                isDeleteSameImage.value = true
            }
        }
    }


}
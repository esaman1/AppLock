package com.ezteam.applocker.viewmodel

import android.app.Application
import android.app.RecoverableSecurityException
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.media.MediaScannerConnection
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezteam.applocker.item.ItemVideo
import com.ezteam.applocker.item.ItemVideoHide
import com.ezteam.applocker.key.Vault
import com.ezteam.applocker.utils.CryptoUtil
import com.ezteam.applocker.utils.IOUtil
import com.ezteam.applocker.utils.ImageUtil
import com.ezteam.baseproject.extensions.getFileSize
import com.ezteam.baseproject.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class HideVideoModel(application: Application) : BaseViewModel(application) {
    val isHide: MutableLiveData<Boolean> = MutableLiveData()
    val videos: MutableLiveData<ItemVideo> = MutableLiveData()
    val videoHideSelected: MutableLiveData<MutableList<ItemVideoHide>?> = MutableLiveData()
    val listVideoHide: MutableLiveData<MutableList<ItemVideoHide>?> = MutableLiveData()
    val recoverVideo: MutableLiveData<String> = MutableLiveData()

    fun getVideoGallery(context: Context) {
        val uri = MediaStore.Video.Media.DATA
        // if GetImageFromThisDirectory is the name of the directory from which image will be retrieved
        val projection = arrayOf(
            uri, MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.SIZE
        )
        try {
            val cursor = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                null, null, MediaStore.Video.Media.DATE_ADDED + " DESC"
            )
            if (cursor!=null) {
                val isDataPresent = cursor.moveToFirst()
                if (isDataPresent) {
                    do {
                        val file = File(cursor.getString(cursor.getColumnIndex(uri)))
                        viewModelScope.launch(Dispatchers.Main) {
                            try {
                                val video = ItemVideo(file)
                                video.duration = file.getMediaDuration(context)
                                videos.value = video
                            } catch (e: IllegalArgumentException) {
                            } catch (r: RuntimeException) {
                            }

                        }
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IllegalArgumentException::class, RuntimeException::class)
    private fun File.getMediaDuration(context: Context): Long {
        if (!exists()) return 0
        val inputStream = FileInputStream(absolutePath)
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(inputStream.fd)
        val duration = retriever.extractMetadata(METADATA_KEY_DURATION)
        retriever.release()
        return duration?.toLongOrNull() ?: 0
    }


    fun encryptVideo(listVideo: MutableList<ItemVideo>, context: Context, path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = mutableListOf<ItemVideoHide>()
            listVideo.forEach {
                if (it.isSelected) {
                    val file = it.file
                    ImageUtil.encryptFile(
                        context,
                        file.toUri(),
                        file.name,
                        file.getFileSize().toInt(), false, this
                    )
                    val hide = ItemVideoHide(file.name, "${path}//${file.name}")
                    list.add(hide)
                }
            }
            //
            val listVideoHide = mutableListOf<ItemVideoHide>()
            list.forEach {
                if (File(it.thumbnailPath).exists()) {
                    try {
                        val ex = it.fileName.split(".")
                        ex[ex.size - 1]
                        val file = IOUtil.createExternalSharedFile("", ".${ex[ex.size - 1]}")
                        val decrypted: ByteArray? =
                            CryptoUtil.decrypt(
                                IOUtil.read(File(it.thumbnailPath)),
                                Vault.PW(),
                                it.thumbnailPath
                            )
                        if (decrypted!=null) {
                            IOUtil.write(decrypted, file, context, this)
                        } else {
                            val encryptedData = IOUtil.read(File(it.thumbnailPath))
                            IOUtil.write(
                                CryptoUtil.decrypt(encryptedData, Vault.PW(), it.thumbnailPath),
                                file,
                                context,
                                this
                            )
                        }
                        it.decryptPath = file.absolutePath
//                    viewModelScope.launch(Dispatchers.Main) {
//                        videoHide.value = it
//                    }
                        listVideoHide.add(it)
                    } catch (e: IOException) {
                        viewModelScope.launch(Dispatchers.Main) {
                            Toast.makeText(context, "Cant create file !", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
            isHide.postValue(true)
            videoHideSelected.postValue(listVideoHide)
        }
    }

    fun decryptListVideo(list: MutableList<ItemVideoHide>, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val listVideo = mutableListOf<ItemVideoHide>()
            list.forEach {
                try {
                    if (File(it.thumbnailPath).exists()) {
                        val ex = it.fileName.split(".")
                        ex[ex.size - 1]
                        val file = IOUtil.createExternalSharedFile("", ".${ex[ex.size - 1]}")
                        val decrypted: ByteArray? =
                            CryptoUtil.decrypt(
                                IOUtil.read(File(it.thumbnailPath)),
                                Vault.PW(),
                                it.thumbnailPath
                            )
                        if (decrypted!=null) {
                            IOUtil.write(decrypted, file, context, this)
                        } else {
                            val encryptedData = IOUtil.read(File(it.thumbnailPath))
                            val decrypt =
                                CryptoUtil.decrypt(encryptedData, Vault.PW(), it.thumbnailPath)
                            if (decrypt!=null) {
                                IOUtil.write(decrypt, file, context, this)
                            } else {
                                viewModelScope.launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Cant create file !",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        }
                        it.decryptPath = file.absolutePath
                        listVideo.add(it)
                    }
                } catch (e: IOException) {

                }
            }
            listVideoHide.postValue(listVideo)
        }
    }

    fun deleteVideo(path: String, activity: AppCompatActivity) {
        viewModelScope.launch(Dispatchers.IO) {
            //        val index = binding.viewPager.currentItem
            MediaScannerConnection.scanFile(
                activity, arrayOf(path), null
            ) { _, uri ->
                try {
                    uri?.let {
                        if (activity.contentResolver.delete(it, null, null)!=-1) {
//                    remove success
                        } else {
//                    toast(getString(R.string.app_error))
                        }
                    }
                } catch (exception: SecurityException) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (exception is RecoverableSecurityException) {
                            val editPendingIntent =
                                MediaStore.createDeleteRequest(activity.contentResolver,
                                    arrayOf(uri).map { it })
                            activity.startIntentSenderForResult(
                                editPendingIntent.intentSender, Vault.REQUEST_CODE_DELETE_VIDEO,
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

    fun recoverVideo(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(path)
            if (file.exists()) {
                (file.delete())
            }
        }
    }
}
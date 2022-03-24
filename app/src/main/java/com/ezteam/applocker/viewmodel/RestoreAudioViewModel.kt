package com.ezteam.applocker.viewmodel

import android.app.Application
import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezteam.applocker.item.ItemAlbumRestoreAudios
import com.ezteam.applocker.item.ItemAudioRestore
import com.ezteam.applocker.utils.Config
import com.ezteam.applocker.utils.FileUtils
import com.ezteam.baseproject.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class RestoreAudioViewModel(application: Application) : BaseViewModel(application) {
    private val listAudios = mutableListOf<ItemAudioRestore>()
    val position: MutableLiveData<Int> = MutableLiveData()
    var albumRestoreAudioLiveData: MutableLiveData<ItemAlbumRestoreAudios?> = MutableLiveData()
    var listAlbumRestoreAudioLiveData: MutableLiveData<MutableList<ItemAlbumRestoreAudios>?> =
        MutableLiveData()

    fun getAudiosRestore(context: Context) {
        listAudios.clear()
        val strArr = Environment.getExternalStorageDirectory().absolutePath
        viewModelScope.launch(Dispatchers.IO) {
            getSdCardAudio(context)
            val arr = File(strArr).listFiles()
            if (!arr.isNullOrEmpty()) {
                checkFileOfDirectoryAudio(strArr, arr)
            }
        }
    }

    private fun getSdCardAudio(context: Context) {
        val externalStoragePaths = FileUtils.getExternalStorageDirectories(context)
        if (externalStoragePaths.isNotEmpty()) {
            for (path in externalStoragePaths) {
                val file = File(path)
                if (file.exists()) {
                    val subFiles = file.listFiles()
                    subFiles?.let {
                        checkFileOfDirectoryAudio(path, it)
                    }
                }
            }
        }
    }

    private fun checkFileOfDirectoryAudio(temp: String, fileArr: Array<File>) {
        for (i in fileArr.indices) {
            if (fileArr[i].isDirectory) {
                val temp_sub = fileArr[i].path
                val mfileArr = File(fileArr[i].path).listFiles()
                if (mfileArr!=null&&mfileArr.isNotEmpty()) checkFileOfDirectoryAudio(
                    temp_sub,
                    mfileArr
                )
            } else {
                if (fileArr[i].path.endsWith(".mp3")
                    ||fileArr[i].path.endsWith(".aac")
                    ||fileArr[i].path.endsWith(".amr")
                    ||fileArr[i].path.endsWith(".m4a")
                    ||fileArr[i].path.endsWith(".ogg")
                    ||fileArr[i].path.endsWith(".wav")
                    ||fileArr[i].path.endsWith(".flac")
                ) {
                    val file = File(fileArr[i].path)
                    var duration: Long = 0
                    val retriever = MediaMetadataRetriever()
                    try {
                        retriever.setDataSource(file.path)
                        duration =
                            (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                ?: "0").toLong()
                        retriever.release()
                    } catch (e: Exception) {
                    }
                    val file_size = file.length().toString().toLong()
                    if (file_size > 10000) {
                        listAudios.add(
                            ItemAudioRestore(
                                fileArr[i].path,
                                file.lastModified(),
                                file.length(),
                                duration
                            )
                        )
                    }
                }
            }
        }
        if (listAudios.isNotEmpty()&&!temp.contains(
                FileUtils.getPathSave(
                    FileUtils.getPathSave(
                        Config.RESTORE_AUDIO
                    )
                )
            )
        ) {
            viewModelScope.launch(Dispatchers.Main) {
                val listAudioCopy = listAudios.toMutableList()
                listAudioCopy.sortWith { a, b -> if (a.lastModified > b.lastModified) 1 else if (a.lastModified < b.lastModified) -1 else 0 }
                albumRestoreAudioLiveData.value = ItemAlbumRestoreAudios(
                    temp,
                    File(temp).lastModified(),
                    listAudioCopy
                )
                listAudios.clear()
            }
        }
    }
}
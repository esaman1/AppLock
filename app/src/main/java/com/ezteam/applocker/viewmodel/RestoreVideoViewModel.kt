package com.ezteam.applocker.viewmodel

import android.app.Application
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezteam.applocker.item.ItemAlbumRestoreVideos
import com.ezteam.applocker.item.ItemVideoRestore
import com.ezteam.applocker.utils.Config
import com.ezteam.applocker.utils.FileUtils
import com.ezteam.baseproject.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class RestoreVideoViewModel(application: Application) : BaseViewModel(application) {
    private val listVideos = mutableListOf<ItemVideoRestore>()
    val position: MutableLiveData<Int> = MutableLiveData()
    var albumRestoreVideosLiveData: MutableLiveData<ItemAlbumRestoreVideos?> = MutableLiveData()
    var listAlbumRestoreVideosLiveData: MutableLiveData<MutableList<ItemAlbumRestoreVideos>?> =
        MutableLiveData()

    fun getVideoRestore(context: Context) {
        listVideos.clear()
        val strArr = Environment.getExternalStorageDirectory().absolutePath
        viewModelScope.launch(Dispatchers.IO) {
            getSdCardVideo(context)
            val arr = File(strArr).listFiles()
            if (!arr.isNullOrEmpty()) {
                checkFileOfDirectoryVideo(strArr, arr)
            }
//            listAlbumRestoreVideos.sortWith { a, b -> if (a.lastModified > b.lastModified) 1 else if (a.lastModified < b.lastModified) -1 else 0 }
        }
    }

    private fun getSdCardVideo(context: Context) {
        val externalStoragePaths = FileUtils.getExternalStorageDirectories(context)
        if (externalStoragePaths.isNotEmpty()) {
            for (path in externalStoragePaths) {
                val file = File(path)
                if (file.exists()) {
                    val subFiles = file.listFiles()
                    checkFileOfDirectoryVideo(path, subFiles)
                }
            }
        }
    }

    private fun checkFileOfDirectoryVideo(temp: String, fileArr: Array<File>?) {
        if (fileArr!=null) {
            for (i in fileArr.indices) {
                if (fileArr[i].isDirectory) {
                    val temp_sub = fileArr[i].path
                    val mfileArr = File(fileArr[i].path).listFiles()
                    if (mfileArr!=null&&mfileArr.isNotEmpty())
                        checkFileOfDirectoryVideo(temp_sub, mfileArr)
                } else {
                    if (fileArr[i].path.endsWith(".3gp")
                        ||fileArr[i].path.endsWith(".mp4")
                        ||fileArr[i].path.endsWith(".mkv")
                        ||fileArr[i].path.endsWith(".flv")
                    ) {
                        val file = File(fileArr[i].path)
                        val type = fileArr[i].path.substring(fileArr[i].path.lastIndexOf(".") + 1)
                        var duration: Long = 0
                        val retriever = MediaMetadataRetriever()
                        try {
                            retriever.setDataSource(file.path)
                            duration =
                                (retriever.extractMetadata(METADATA_KEY_DURATION) ?: "0").toLong()
                            retriever.release()
                        } catch (e: Exception) {
                        }
                        listVideos.add(
                            ItemVideoRestore(
                                fileArr[i].path,
                                file.lastModified(),
                                file.length(),
                                type,
                                duration
                            )
                        )
                    }
                }
            }
            if (listVideos.isNotEmpty()&&!temp.contains(FileUtils.getPathSave(Config.RESTORE_VIDEO))) {
                val listVideoCopy = listVideos.toMutableList()
//                listVideoCopy.sortWith { a, b -> if (a.lastModified > b.lastModified) 1 else if (a.lastModified < b.lastModified) -1 else 0 }
                viewModelScope.launch(Dispatchers.Main) {
                    albumRestoreVideosLiveData.value = ItemAlbumRestoreVideos(
                        temp,
                        File(temp).lastModified(),
                        listVideoCopy
                    )
                }
                listVideos.clear()
            }
        }
    }
}
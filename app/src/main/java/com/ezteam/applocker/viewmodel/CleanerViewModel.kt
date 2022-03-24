package com.ezteam.applocker.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezteam.applocker.utils.CleanUtils
import com.ezteam.applocker.utils.CleanUtils.readIntConfig
import com.ezteam.baseproject.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.util.*

class CleanerViewModel(application: Application) : BaseViewModel(application) {
    val sumStr: MutableLiveData<String?> = MutableLiveData()
    var sumInt = 0L
    var countSuccess = 0
    fun cleanFile(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            var delete_file_size: Int? = 0
            val list: String =
                CleanUtils.getFilesFromDist(Environment.getExternalStorageDirectory()).toString()
            try {
                delete_file_size = removeSubFiles(JSONArray(list))
            } catch (e: Exception) {
            }
            CleanUtils.writeIntConfig(context, "delete_file_size", delete_file_size)
            sumInt += readIntConfig(context, "delete_file_size") + getAlfaRange(0, 3) * 1024 * 2014
            countSuccess++
            checkDone()
        }
    }

    fun cleanData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            val bitsAvailable: Long = stat.blockSizeLong * stat.availableBlocksLong
            val megAvailable = (bitsAvailable / (1024 * 1024 * 8)).toInt()

            try { //TODO update for new methods
                val fileName = File(Environment.getExternalStorageDirectory(), "wipe.dat")
                val fos = FileOutputStream(fileName)
                fos.write(megAvailable)
                fos.close()
                fileName.delete()
            } catch (e: java.lang.Exception) {
            }
            try {
                val curfile = File(Environment.getExternalStorageDirectory(), "wipe.dat")
                curfile.delete()
            } catch (e: java.lang.Exception) {
                Log.i("===", e.toString())
            }
            CleanUtils.writeIntConfig(context, "delete_wipe_size", megAvailable)
            sumInt += readIntConfig(context, "delete_wipe_size") + getAlfaRange(0, 3) * 1024 * 2014
            countSuccess++
            checkDone()
        }
    }

    fun cleanOptimizes(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            CleanUtils.writeIntConfig(context, "delete_optim_size", 0)
            sumInt += readIntConfig(context, "delete_optim_size") + getAlfaRange(
                0,
                10
            ) * 1024 * 2014
            countSuccess++
            checkDone()
        }
    }

    private fun checkDone() {
        if (countSuccess==3) {
            countSuccess = 0
            viewModelScope.launch(Dispatchers.Main) {
                sumStr.value = CleanUtils.bytesToHuman(sumInt)
                sumInt = 0
            }
        }
    }

    private fun getAlfaRange(min: Int, max: Int): Int {
        require(min < max) { "max must be greater than min" }
        val r = Random()
        return r.nextInt(max - min + 1) + min
    }

    @SuppressLint("SetTextI18n")
    private fun removeSubFiles(jas: JSONArray): Int {
        var delete_file_size = 0
        try {
            //JSONArray jas = new JSONArray(list);
            for (i in 0 until jas.length()) {
                if (jas.getJSONObject(i).getString("type")=="dir") {
                    delete_file_size += removeSubFiles(
                        jas.getJSONObject(i).getJSONArray("parent")
                    )
                    //sleep(300);
                } else {
                    if (jas.getJSONObject(i).has("path")) {
                        val file = jas.getJSONObject(i).getString("path")
                        val curfile = File(file)
                        if (curfile.isFile) {
                            val file_size = curfile.length().toInt()
                            if (file.endsWith(".apk")||file.endsWith(".log")||file.endsWith(".tmp")) try {
                                val diff = Date().time - curfile.lastModified()
                                if (diff > 1 * 24 * 60 * 60 * 1000) {
                                    if (curfile.delete()) {
                                        delete_file_size += file_size
                                    }
                                }
                            } catch (ignored: java.lang.Exception) {
                            }
                        }
                        Log.i("===", "file : $file")

                        //prViewData.setText(file);
                        //sleep(1000);
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("===", e.toString())
        }
        return delete_file_size
    }
}
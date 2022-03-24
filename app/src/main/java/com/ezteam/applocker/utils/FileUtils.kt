package com.ezteam.applocker.utils

import android.content.Context
import android.os.Environment
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.IOException
import java.io.InputStream

object FileUtils {
    fun getExternalStorageDirectories(context: Context): MutableList<String> {
        val results: MutableList<String> = mutableListOf()
        val externalDirs = context.getExternalFilesDirs(null)
        if (externalDirs!=null&&externalDirs.isNotEmpty()) {
            for (file in externalDirs) {
                if (file!=null) {
                    val paths = file.path.split("/Android".toRegex()).toTypedArray()
                    if (paths.isNotEmpty()) {
                        val path = paths[0]
                        val addPath: Boolean = Environment.isExternalStorageRemovable(file)
                        if (addPath) {
                            results.add(path)
                        }
                    }
                }
            }
        }
        if (results.isEmpty()) {
            var output = ""
            var `is`: InputStream? = null
            try {
                val process =
                    ProcessBuilder(*arrayOfNulls(0)).command(*arrayOf("mount | grep /dev/block/vold"))
                        .redirectErrorStream(true).start()
                process.waitFor()
                `is` = process.inputStream
                val buffer = ByteArray(1024)
                while (`is`.read(buffer)!=-1) {
                    output += String(buffer)
                }
                `is`.close()
            } catch (e: Exception) {
                if (`is`!=null) {
                    try {
                        `is`.close()
                    } catch (e2: IOException) {
                    }
                }
            }
            if (output.trim { it <= ' ' }.isNotEmpty()) {
                val devicePoints: Array<String> =
                    output.split(IOUtils.LINE_SEPARATOR_UNIX).toTypedArray()
                if (devicePoints.isNotEmpty()) {
                    for (voldPoint in devicePoints) {
                        results.add(voldPoint.split(" ".toRegex()).toTypedArray()[2])
                    }
                }
            }
        }
        val storageDirectories = mutableListOf<String>()
        if (!results.isNullOrEmpty()) {
            for (i in results.indices) {
                storageDirectories.add(results[i])
            }
        }
        return storageDirectories
    }

    fun getPathSave(path: String): String {
        val sbDirectory = StringBuilder()
        sbDirectory.append(Environment.getExternalStorageDirectory())
        sbDirectory.append(File.separator)
        sbDirectory.append("FileRecovery")
        sbDirectory.append(File.separator)
        sbDirectory.append(path)
        return sbDirectory.toString()
    }
}
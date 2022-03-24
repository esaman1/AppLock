package com.ezteam.applocker.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ImageSaverUtils(private val context: Context) {
    var directoryName = ""
    var fileName = ""

    fun save(bitmapImage: Bitmap) {
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(createFile())
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun createFile(): File {
        val directory: File = context.getDir(directoryName, Context.MODE_PRIVATE)

        if (!directory.exists() && !directory.mkdirs()) {
        }
        return File(directory, fileName)
    }

    fun load(): Bitmap? {
        var inputStream: FileInputStream? = null
        try {
            inputStream = FileInputStream(createFile())
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }
}
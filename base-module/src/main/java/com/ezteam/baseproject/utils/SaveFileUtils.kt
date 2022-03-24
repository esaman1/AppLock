package com.ezteam.baseproject.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

object SaveFileUtils {
    fun saveBodyFile(body: ResponseBody?, path: String): String {
        if (body == null)
            return ""
        var input: InputStream? = null
        try {
            input = body.byteStream()
            val fos = FileOutputStream(path)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return path
        } catch (e: Exception) {
            Log.e("saveFile", e.toString())
        } finally {
            input?.close()
        }
        return ""
    }


    fun saveImageBitmap(context: Context, folderName: String, bitmap: Bitmap?): Uri? {
        val filename = "IMG_${System.currentTimeMillis()}.png"
        var fos: OutputStream?
        var imageUri: Uri?
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val newFile = File(getFolderParent(folderName) + File.separator + filename)
            fos = FileOutputStream(newFile)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                newFile.absolutePath,
                newFile.name,
                newFile.name
            )
            imageUri = Uri.fromFile(newFile)
            context.sendBroadcast(
                Intent(
                    "android.intent.action.MEDIA_SCANNER_SCAN_FILE",
                    imageUri
                )
            )
        } else {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + folderName
                )
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
            context.contentResolver.also { resolver ->
                imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }

            fos?.use { bitmap?.compress(Bitmap.CompressFormat.PNG, 100, it) }

            contentValues.clear()
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
            imageUri?.let { context.contentResolver.update(it, contentValues, null, null) }
        }
        return imageUri
    }

    fun getFolderParent(name: String): String? {
        val mediaStorageDir = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + Environment.DIRECTORY_PICTURES, name
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return ""
            }
        }
        return mediaStorageDir.absolutePath
    }
}
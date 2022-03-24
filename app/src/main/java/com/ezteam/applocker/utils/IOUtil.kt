package com.ezteam.applocker.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*

/**
 * ------------------------------------
 *
 *
 * Author: Yongjie Zhuang
 *
 *
 * ------------------------------------
 *
 *
 * Class that is responsible for managing I/O operations.
 *
 */
object IOUtil {
    /**
     * Read all bytes from file
     *
     * @param file file
     * @return bytes of the file
     * @throws IOException
     */
    @Throws(IOException::class)
    fun read(file: File): ByteArray? {
        if (file.isFile||file.isDirectory) {
            FileInputStream(file).use { `in` ->
                return read(
                    `in`, file.length()
                        .toInt()
                )
            }
        }
        return null
    }

    /**
     * Read bytes from an input stream
     *
     * @param in  input stream
     * @param len number of bytes should be read
     * @return bytes
     */
    @Throws(IOException::class)
    fun read(`in`: InputStream, len: Int): ByteArray? {
        return try {
            val bytes = ByteArray(len)
            `in`.read(bytes)
            bytes
        } catch (e: OutOfMemoryError) {
            null
        }
    }

    /**
     * Write all bytes to file
     *
     * @param bytes binary data
     * @param file  file
     * @throws IOException
     */
    @Throws(IOException::class)
    fun write(
        bytes: ByteArray?,
        file: File?,
        context: Context? = null,
        coroutineScope: CoroutineScope? = null
    ) {
        try {
            if (file!=null&&bytes!=null) {
                FileOutputStream(file).use { out -> out.write(bytes) }
            } else {
                coroutineScope?.launch(Dispatchers.Main) {
                    context?.let {
                        Toast.makeText(it, "Cant create file !", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            coroutineScope?.launch(Dispatchers.Main) {
                context?.let {
                    Toast.makeText(it, "Cant create file !", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Write all bytes to a file that will be created in internal storage
     *
     * @param bytes       bytes
     * @param fileOutName name of the file that the data are written to in internal
     * storage
     * @param context     context
     * @throws IOException
     */
    @Throws(IOException::class)
    fun write(bytes: ByteArray?, fileOutName: String?, context: Context) {
        context.openFileOutput(fileOutName, Context.MODE_PRIVATE).use { out -> out.write(bytes) }
    }

    /**
     * Create temp file with an extension '.t' that has a name created like this:
     * '`"PIC" + DateUtil.getDateTimeStr()`'. This temp file tho is not protected in internal
     * storage, it can be unavailable to other apps as well.
     *
     * @param context
     * @return a temp file
     */
    @Throws(IOException::class)
    fun createTempFile(context: Context): File {
        val filename = "PIC" + DateUtil.dateStr
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(filename, ".t", storageDir)
    }

    /**
     * Create temp file with the specified filename and file extension. This file is created in
     * external storage public directory (DIRECTORY_PICTURES), thus it can be visible to the user
     * as well as other apps. This method requires `Manifest.permission.WRITE_EXTERNAL_STORAGE`
     *
     * @param filename
     * @param fileExtension
     * @return a temp file
     */
    @Throws(IOException::class)
    fun createExternalSharedFile(
        filename: String,
        fileExtension: String
    ): File {
        var filename = filename
        var fileExtension = fileExtension
        if (!fileExtension.startsWith(".")) fileExtension = ".$fileExtension"
        if (filename.isEmpty()) filename = "PIC" + DateUtil.dateStr
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(filename, fileExtension, storageDir)
    }

    /**
     * Attempt to delete a file
     *
     * @param file
     * @return whether the file is deleted
     */
    fun deleteFile(file: File): Boolean {
        return if (file.delete()) {
            true
        } else !file.exists()
    }

    /**
     * Attempt to delete a file
     *
     * @param path
     * @return whether the file is deleted
     */
    fun deleteFile(path: String?): Boolean {
        val file = File(path)
        return deleteFile(file)
    }
}
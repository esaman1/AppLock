package com.ezteam.applocker.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.opengl.GLES30
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.ezteam.applocker.R
import com.ezteam.applocker.item.ItemImageHide
import com.ezteam.applocker.item.ItemVideoHide
import com.ezteam.applocker.key.Vault
import com.orhanobut.hawk.Hawk
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
 * Util Class for image decoding
 *
 */
object ImageUtil {
    var THUMBNAIL_EXTENSION = "_THUMBNAIL"
    var THUMBNAIL_SIZE = 200
    // TODO: this does not seem to work properly, fix it
    /**
     * Decode the bytes into bitmap based on the required size.
     *
     * @param data      image data
     * @param reqWidth  required width
     * @param reqHeight required height
     * @return bitmap that is decoded.
     */
    private fun decodeBitmapWithScaling(data: ByteArray, reqWidth: Int, reqHeight: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        // for getting the outWidth and outHeight
        val imgWidth = options.outWidth
        val imgHeight = options.outHeight

        // sub-sampling if necessary
        var inSampleSize = 1
        if (imgHeight > reqHeight||imgWidth > reqWidth) {
            val halfHeight = imgHeight / 2
            val halfWidth = imgWidth / 2
            while (halfWidth / inSampleSize >= reqWidth||halfHeight / inSampleSize >= reqHeight) {
                // always set to the power of 2
                inSampleSize *= 2
            }
        }
        options.inSampleSize = inSampleSize
        options.inJustDecodeBounds = false
        return try {
            BitmapFactory.decodeByteArray(data, 0, data.size, options)
        } catch (e: OutOfMemoryError) {
            null
        }
    }

    /**
     * Decode to bytes to bitmap without any downscaling
     *
     * @param data image data
     * @return decoded bitmap
     */
    fun decodeBitmap(data: ByteArray): Bitmap? {
        return try {
            val options = BitmapFactory.Options()
            BitmapFactory.decodeByteArray(data, 0, data.size, options)
        } catch (e: OutOfMemoryError) {
            null
        }
    }

    fun decryptNDisplay(thumbnailPath: String): Bitmap? {
        // get the path to the decrypted image, decrypt data and display
        try {
            // read encrypted data
            val encryptedData = IOUtil.read(File(thumbnailPath))
            // decrypt the data
            val decrypted: ByteArray? =
                CryptoUtil.decrypt(encryptedData, Vault.PW(), thumbnailPath)
            // get the allowed maximum size of texture in OpenGL ES3.0
            val maxsize = IntArray(1)
            GLES30.glGetIntegerv(GLES30.GL_MAX_TEXTURE_SIZE, maxsize, 0)
            val reqWidth: Int
            val reqHeight: Int = maxsize[0]
            reqWidth = reqHeight

            // decode and downscale if needed to avoid OutOfMemory exception
            decrypted?.let {
                return decodeBitmapWithScaling(decrypted, reqWidth, reqHeight)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * Convert a Bitmap to byte[]
     *
     * @param bitmap
     */
    fun toBytes(bitmap: Bitmap): ByteArray {
        val buffer = ByteArrayOutputStream(bitmap.allocationByteCount)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer)
        return buffer.toByteArray()
    }

    //
    fun encryptFile(
        context: Context,
        uri: Uri,
        name: String,
        size: Int,
        isImage: Boolean,
        coroutineScope: CoroutineScope
    ) {
        try {
            context.contentResolver.openInputStream(uri).use { `in` ->
                `in`?.let { encryptImage(context, it, name, size, isImage, coroutineScope) }
            }
        } catch (e1: FileNotFoundException) {
            e1.printStackTrace()
            coroutineScope.launch(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    context.getString(R.string.file_not_found),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } catch (e2: IOException) {
            e2.printStackTrace()
            coroutineScope.launch(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    context.getString(R.string.cannot_read_file),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun encryptImage(
        context: Context,
        `in`: InputStream,
        filename: String,
        filesize: Int,
        isImage: Boolean, coroutineScope: CoroutineScope
    ) {
        try {
            if (isImage) {
                val listFile = Hawk.get(Vault.KEY_FILE_NAME_IMAGE, mutableListOf<ItemImageHide>())
                listFile.forEach {
                    if (it.fileName==filename) {
                        coroutineScope.launch(Dispatchers.Main) {
                            toast(context)
                        }
                        return
                    }
                }
                listFile.add(ItemImageHide(filename, "${context.filesDir.path}//$filename"))
                Hawk.put(Vault.KEY_FILE_NAME_IMAGE, listFile)
            } else {
                val listFile = Hawk.get(Vault.KEY_FILE_NAME_VIDEO, mutableListOf<ItemVideoHide>())
                listFile.forEach {
                    if (it.fileName==filename) {
                        coroutineScope.launch(Dispatchers.Main) {
                            toast(context)
                        }
                        return
                    }
                }
                listFile.add(ItemVideoHide(filename, "${context.filesDir.path}//$filename"))
                Hawk.put(Vault.KEY_FILE_NAME_VIDEO, listFile)
            }
            encryptNWrite(context, `in`, filename, filesize, isImage, coroutineScope)

        } catch (e2: IOException) {
            e2.printStackTrace()
            coroutineScope.launch(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    context.getString(R.string.file_not_read_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun toast(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.file_hidden),
            Toast.LENGTH_SHORT
        ).show()
    }

    @Throws(IOException::class)
    private fun encryptNWrite(
        context: Context,
        `in`: InputStream,
        filename: String,
        filesize: Int,
        isImage: Boolean, coroutineScope: CoroutineScope?,
    ) {
        // read the image
        val rawData: ByteArray? = IOUtil.read(`in`, filesize)
        val pw = Vault.PW()
        if (rawData!=null) {
            if (isImage) {
                // downscale image for thumbnail
                var bitmap: Bitmap? = decodeBitmap(rawData)
                bitmap = ThumbnailUtils.extractThumbnail(
                    bitmap,
                    ImageUtil.THUMBNAIL_SIZE,
                    ImageUtil.THUMBNAIL_SIZE
                )
                val thumbnail: ByteArray? = if (bitmap!=null) toBytes(bitmap) else null
                // key
                // encrypt image & thumbnail
                val encryptedData: ByteArray? =
                    CryptoUtil.encrypt(rawData, pw, "${context.filesDir.path}//$filename")
                val encryptedThumbnail: ByteArray? =
                    CryptoUtil.encrypt(thumbnail, pw, "${context.filesDir.path}//$filename")

                // write encrypted image to internal storage
                if (encryptedData!=null) {
                    IOUtil.write(encryptedData, filename, context)
                } else {
                    coroutineScope?.let {
                        it.launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Cant hide file !",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
                // write thumbnail to internal storage
                if (encryptedThumbnail!=null) {
                    IOUtil.write(
                        encryptedThumbnail,
                        filename + ImageUtil.THUMBNAIL_EXTENSION,
                        context
                    )
                } else {
                    coroutineScope?.let {
                        it.launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Cant hide file !",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            } else {
                // encrypt image & thumbnail
                val encryptedData: ByteArray? =
                    CryptoUtil.encrypt(rawData, pw, "${context.filesDir.path}//$filename")
                encryptedData?.let {
                    IOUtil.write(it, filename, context)
                }
            }
        } else {
            coroutineScope?.let {
                it.launch(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Cant hide file !",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    fun recoverImage(imageName: String, context: Context, thumbnailPath: String): Boolean {
        return try {
//             create temp file
            val ex = imageName.split(".")
            val file = IOUtil.createExternalSharedFile("", ".${ex[ex.size - 1]}")
            val decrypted: ByteArray? =
                CryptoUtil.decrypt(IOUtil.read(File(thumbnailPath)), Vault.PW(), thumbnailPath)
            if (decrypted!=null) {
                IOUtil.write(decrypted, file)
            } else {
                val encryptedData = IOUtil.read(File(thumbnailPath))
                IOUtil.write(CryptoUtil.decrypt(encryptedData, Vault.PW(), thumbnailPath), file)
            }
            // expose it to Gallery
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
//            val bmOptions = BitmapFactory.Options()
//            val bitmap = BitmapFactory.decodeFile(thumbnailPath, bmOptions)
//            saveImageBitmap(context, Config.RESTORE_IMAGE_HIDE, bitmap, imageName)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun recoverFileVideo(file: File, context: Context): Boolean {
        return try {
            val newFile = File(initFolderParent(Config.RESTORE_VIDEO_HIDE), file.name)
            if (!newFile.exists()) {
                newFile.createNewFile()
            }
            copy(file, newFile)
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(newFile)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun recoverListVideo(listFile: MutableList<File>, context: Context) {
        listFile.forEach {
            try {
                val newFile = File(initFolderParent(Config.RESTORE_VIDEO), it.name)
                if (!newFile.exists()) {
                    newFile.createNewFile()
                }
                copy(it, newFile)
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val contentUri = Uri.fromFile(newFile)
                mediaScanIntent.data = contentUri
                context.sendBroadcast(mediaScanIntent)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun recoverListAudios(listFile: MutableList<File>, context: Context) {
        listFile.forEach {
            try {
                val newFile = File(initFolderParent(Config.RESTORE_AUDIO), it.name)
                if (!newFile.exists()) {
                    newFile.createNewFile()
                }
                copy(it, newFile)
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val contentUri = Uri.fromFile(newFile)
                mediaScanIntent.data = contentUri
                context.sendBroadcast(mediaScanIntent)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    fun copy(src: File?, dst: File?) {
        FileInputStream(src).use { `in` ->
            FileOutputStream(dst).use { out ->
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            }
        }
    }

    fun recoverListFileImage(listFile: MutableList<File>, context: Context) {
        listFile.forEach {
            val bmOptions = BitmapFactory.Options()
            val bitmap = BitmapFactory.decodeFile(it.absolutePath, bmOptions)
            //
            saveImageBitmap(
                context,
                Config.RESTORE_IMAGE,
                bitmap,
                it.name
            )
        }
    }

    fun saveImageBitmap(
        context: Context,
        folderName: String,
        bitmap: Bitmap,
        filename: String
    ): Uri? {
        var fos: OutputStream?
        var imageUri: Uri?
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val newFile = File(initFolderParent(folderName), filename)
            if (!newFile.exists()) {
                newFile.createNewFile()
            }
            fos = try {
                FileOutputStream(newFile)
            } catch (ex: Exception) {
                FileOutputStream(newFile.path)
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos?.flush()
            fos?.close()
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
            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + File.separator + folderName
                    )
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                context.contentResolver.also { resolver ->
                    imageUri =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let {
                        resolver.openOutputStream(it)
                    }
                }

                fos?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                fos?.close()
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                imageUri?.let { context.contentResolver.update(it, contentValues, null, null) }
                return imageUri
            } catch (ex: Exception) {
                return null
            }

        }
        return null
    }

    private fun initFolderParent(name: String): String? {
        val mediaDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            name
        )
        if (Environment.getExternalStorageState()==Environment.MEDIA_MOUNTED&&!mediaDirectory.isDirectory) {
            mediaDirectory.mkdirs()
        }
        return mediaDirectory.path
    }
}
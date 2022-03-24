package com.ezteam.baseproject.extensions

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.ezteam.baseproject.R
import com.ezteam.baseproject.utils.DateUtils
import java.io.File
import java.lang.Exception
import java.math.BigDecimal
import java.util.*


fun File.isPdf(): Boolean {
    return path.endsWith(".pdf")
}

fun File.isVideo(): Boolean {
    return path.endsWith(".mp4") || path.endsWith(".3gp")
}

fun File.isImage(): Boolean {
    return path.endsWith(".jpg") ||
            path.endsWith(".png") ||
            path.endsWith(".gif") ||
            path.endsWith(".jpeg")
}

fun File.getThumbnail(): Bitmap? {
    return ThumbnailUtils.createVideoThumbnail(
        path,
        MediaStore.Images.Thumbnails.MINI_KIND
    )
}

val File.infoDetail: String
    get() {
        val stringBuilder = StringBuilder()
        stringBuilder.append("<b>File Name:</b> $name")
        stringBuilder.append("<br/><br/>")
        stringBuilder.append("<b>Path:</b> $path")
        stringBuilder.append("<br/><br/>")
        stringBuilder.append(
            "<b>Last modified:</b> " + DateUtils.longToDateString(
                lastModified(), DateUtils.DATE_FORMAT_5
            )
        )
        stringBuilder.append("<br/><br/>")
        stringBuilder.append("<b>Size:</b> ${getFileLength()}")
        return stringBuilder.toString()
    }

fun File.getFileLength(): String {
    val d1 = length() / 1024.0
    val d2 = d1 / 1024.0
    val d3 = d2 / 1024.0
    if (length() < 1024.0) {
        return "${length()} Bytes"
    }
    if (d1 < 1024.0) {
        return BigDecimal(d1).setScale(2, 4).toString() + " KB"
    }
    return if (d2 < 1024.0) {
        BigDecimal(d2).setScale(2, 4).toString() + " MB"
    } else BigDecimal(d3).setScale(2, 4).toString() + " GB"
}

fun File.openFile(context: Context) {
    if (this.endsWith(".apk")) {
        installApp(context)
    } else {
        try {
            val uri: Uri =
                FileProvider.getUriForFile(context, context.packageName + ".provider", this)
            val mime: String? = context.contentResolver.getType(uri)
            context.startActivity(Intent().apply {
                action = Intent.ACTION_VIEW
                setDataAndType(uri, mime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            })
        } catch (ex: Exception) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.cant_open_file),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

fun File.installApp(context: Context) {
    val uri = this.uriFromFile(context)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/vnd.android.package-archive")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(intent)
}

fun File.uriFromFile(context: Context): Uri? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            this
        )
    } else {
        Uri.fromFile(this)
    }
}

fun File.getFileSize(): Long {
    if (!this.exists()) return 0
    if (!this.isDirectory) return this.length()
    val dirs: MutableList<File> = LinkedList()
    dirs.add(this)
    var result: Long = 0
    while (dirs.isNotEmpty()) {
        val dir = dirs.removeAt(0)
        if (!dir.exists()) continue
        val listFiles = dir.listFiles()
        if (listFiles == null || listFiles.isEmpty()) continue
        for (child in listFiles) {
            result += child.length()
            if (child.isDirectory) dirs.add(child)
        }
    }
    return result
}

fun File.share(context: Context, fileType: String? = null) {
    val sharingIntent = Intent(Intent.ACTION_SEND).apply {
        type = fileType ?: "*/*"
        putExtra(Intent.EXTRA_STREAM, Uri.parse(path))
    }
    context.startActivity(Intent.createChooser(sharingIntent, "Share using"))
}

fun MutableList<File>.shareMultiFile(context: Context, fileType: String? = null) {
    val files = ArrayList<Uri>()
    this.forEach {
        files.add(Uri.parse(it.path))
    }
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = fileType ?: "*/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
    }
    context.startActivity(Intent.createChooser(intent, "Share using"))
}

fun MutableList<File>.containOnlyFile(): Boolean {
    this.forEach {
        if (it.isDirectory)
            return false
    }
    return true
}
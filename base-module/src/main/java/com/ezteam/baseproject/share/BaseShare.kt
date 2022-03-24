package com.ezteam.baseproject.share

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.ezteam.baseproject.R
import java.io.File

open class BaseShare {
    companion object {
        const val VIDEO_STREAM = "video/*"
        const val IMAGE_STREAM = "image/*"
    }

    fun shareStream(context: Context, file: File, streamType: String, title: String) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = streamType
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    fun shareStream(
        context: Context,
        file: File,
        streamType: String,
        packages: String,
        title: String
    ) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = streamType
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            `package` = packages
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    fun hasPackage(context: Context, packages: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packages, 0)
            true
        } catch (unused: Exception) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.no_app_response),
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }
}
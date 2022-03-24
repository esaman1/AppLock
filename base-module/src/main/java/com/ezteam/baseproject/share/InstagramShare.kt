package com.ezteam.baseproject.share

import android.content.Context
import java.io.File

class InstagramShare: BaseShare(), ShareFileDelegate {
    companion object {
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"
    }
    override fun shareVideo(context: Context, file: File, title: String) {
        if (hasPackage(context, INSTAGRAM_PACKAGE)) {
            shareStream(context, file, VIDEO_STREAM, INSTAGRAM_PACKAGE, title)
        }
    }

    override fun shareImages(context: Context, file: File, title: String) {
        if (hasPackage(context, INSTAGRAM_PACKAGE)) {
            shareStream(context, file, IMAGE_STREAM, INSTAGRAM_PACKAGE, title)
        }
    }
}
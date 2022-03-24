package com.ezteam.baseproject.share

import android.content.Context
import java.io.File

class TelegramShare: BaseShare(), ShareFileDelegate {
    companion object {
        private const val TELEGRAM_PACKAGE = "org.telegram.messenger"
    }
    override fun shareVideo(context: Context, file: File, title: String) {
        if (hasPackage(context, TELEGRAM_PACKAGE)) {
            shareStream(context, file, VIDEO_STREAM, TELEGRAM_PACKAGE, title)
        }
    }

    override fun shareImages(context: Context, file: File, title: String) {
        if (hasPackage(context, TELEGRAM_PACKAGE)) {
            shareStream(context, file, IMAGE_STREAM, TELEGRAM_PACKAGE, title)
        }
    }
}
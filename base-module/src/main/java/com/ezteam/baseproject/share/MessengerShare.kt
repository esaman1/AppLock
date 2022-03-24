package com.ezteam.baseproject.share

import android.content.Context
import java.io.File

class MessengerShare: BaseShare(), ShareFileDelegate {
    companion object {
        private const val MESSENGER_PACKAGE = "com.facebook.orca"
    }
    override fun shareVideo(context: Context, file: File, title: String) {
        if (hasPackage(context, MESSENGER_PACKAGE)) {
            shareStream(context, file, VIDEO_STREAM, MESSENGER_PACKAGE, title)
        }
    }

    override fun shareImages(context: Context, file: File, title: String) {
        if (hasPackage(context, MESSENGER_PACKAGE)) {
            shareStream(context, file, IMAGE_STREAM, MESSENGER_PACKAGE, title)
        }
    }
}
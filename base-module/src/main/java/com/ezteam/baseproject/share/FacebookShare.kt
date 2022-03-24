package com.ezteam.baseproject.share

import android.content.Context
import java.io.File

class FacebookShare: BaseShare(), ShareFileDelegate {
    companion object {
        private const val FACEBOOK_PACKAGE = "com.facebook.katana"
    }
    override fun shareVideo(context: Context, file: File, title: String) {
        if (hasPackage(context, FACEBOOK_PACKAGE)) {
            shareStream(context, file, VIDEO_STREAM, FACEBOOK_PACKAGE, title)
        }
    }

    override fun shareImages(context: Context, file: File, title: String) {
        if (hasPackage(context, FACEBOOK_PACKAGE)) {
            shareStream(context, file, IMAGE_STREAM, FACEBOOK_PACKAGE, title)
        }
    }
}
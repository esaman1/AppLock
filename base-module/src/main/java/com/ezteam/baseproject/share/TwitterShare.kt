package com.ezteam.baseproject.share

import android.content.Context
import java.io.File

class TwitterShare: BaseShare(), ShareFileDelegate {
    companion object {
        private const val TWITTER_PACKAGE = "com.twitter.android"
    }
    override fun shareVideo(context: Context, file: File, title: String) {
        if (hasPackage(context, TWITTER_PACKAGE)) {
            shareStream(context, file, VIDEO_STREAM, TWITTER_PACKAGE, title)
        }
    }

    override fun shareImages(context: Context, file: File, title: String) {
        if (hasPackage(context, TWITTER_PACKAGE)) {
            shareStream(context, file, IMAGE_STREAM, TWITTER_PACKAGE, title)
        }
    }
}
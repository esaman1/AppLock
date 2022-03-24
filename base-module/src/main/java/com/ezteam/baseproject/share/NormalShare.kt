package com.ezteam.baseproject.share

import android.content.Context
import java.io.File

class NormalShare : BaseShare(), ShareFileDelegate {
    override fun shareVideo(context: Context, file: File, title: String) {
        shareStream(context, file, VIDEO_STREAM, title)
    }

    override fun shareImages(context: Context, file: File, title: String) {
        shareStream(context, file, IMAGE_STREAM, title)
    }
}
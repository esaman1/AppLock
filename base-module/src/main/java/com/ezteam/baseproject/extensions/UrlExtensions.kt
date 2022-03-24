package com.ezteam.baseproject.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

fun String.returnBitmap(): Bitmap? {
    var bitmap: Bitmap? = null
    var imageurl: URL? = null
    try {
        imageurl = URL(this)
    } catch (e: MalformedURLException) {
        e.printStackTrace()
    }
    try {
        val conn = imageurl!!.openConnection() as HttpURLConnection
        conn.doInput = true
        conn.connect()
        val `is` = conn.inputStream
        bitmap = BitmapFactory.decodeStream(`is`)
        `is`.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bitmap
}
package com.ezteam.baseproject.extensions

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.ImageFormat
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Vibrator
import android.util.Size
import android.view.Surface
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.ezteam.baseproject.utils.CompareSizesByArea
import java.text.SimpleDateFormat
import java.util.*

val Context.vibrator: Vibrator?
    get() = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

val Context.wifiManager: WifiManager?
    get() = applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

val Context.clipboardManager: ClipboardManager?
    get() = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

val Context.currentLocale: Locale?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales.get(0)
    } else {
        resources.configuration.locale
    }

const val MIN_PREVIEW_WIDTH = 1280
const val MIN_PREVIEW_HEIGHT = 720
const val MAX_PREVIEW_WIDTH = 1920
const val MAX_PREVIEW_HEIGHT = 1080

fun Context.fragmentActivity(): FragmentActivity? {
    var curContext = this
    var maxDepth = 20
    while (--maxDepth > 0 && curContext !is FragmentActivity) {
        curContext = (curContext as ContextWrapper).baseContext
    }
    return if(curContext is FragmentActivity)
        curContext
    else
        null
}

fun Context.lifecycleOwner(): LifecycleOwner? {
    var curContext = this
    var maxDepth = 20
    while (maxDepth-- > 0 && curContext !is LifecycleOwner) {
        curContext = (curContext as ContextWrapper).baseContext
    }
    return if (curContext is LifecycleOwner) {
        curContext
    } else {
        null
    }
}

fun Context.optimizeSizeCamera(activity: Activity, cameraManager: CameraManager): Size {
    val characteristics: CameraCharacteristics = cameraManager.getCameraCharacteristics("" + CameraCharacteristics.LENS_FACING_FRONT)
    val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
    val sizes = map!!.getOutputSizes(ImageFormat.JPEG)
    val largest = Collections.max(
        listOf(*sizes),
        CompareSizesByArea()
    )

    // Find out if we need to swap dimension to get the preview size relative to sensor
    // coordinate.

    // Find out if we need to swap dimension to get the preview size relative to sensor
    // coordinate.
    val displayRotation: Int = activity.windowManager.defaultDisplay.rotation
    // noinspection ConstantConditions
    // noinspection ConstantConditions
    val mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!!!
    var swappedDimensions = false
    when (displayRotation) {
        Surface.ROTATION_0, Surface.ROTATION_180 -> if (mSensorOrientation == 90 || mSensorOrientation == 270) {
            swappedDimensions = true
        }
        Surface.ROTATION_90, Surface.ROTATION_270 -> if (mSensorOrientation == 0 || mSensorOrientation == 180) {
            swappedDimensions = true
        }
    }

    val displaySize = Point()
    activity.windowManager.defaultDisplay.getSize(displaySize)
    var rotatedPreviewWidth: Int = MIN_PREVIEW_WIDTH
    var rotatedPreviewHeight: Int = MIN_PREVIEW_HEIGHT
    var maxPreviewWidth = displaySize.x
    var maxPreviewHeight = displaySize.y
    if (swappedDimensions) {
        rotatedPreviewWidth = MIN_PREVIEW_HEIGHT
        rotatedPreviewHeight = MIN_PREVIEW_WIDTH
        maxPreviewWidth = displaySize.y
        maxPreviewHeight = displaySize.x
    }

    if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
        maxPreviewWidth = MAX_PREVIEW_WIDTH
    }
    if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
        maxPreviewHeight = MAX_PREVIEW_HEIGHT
    }

    return chooseOptimalSize(
        map.getOutputSizes(SurfaceTexture::class.java),
        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
        maxPreviewHeight, largest)
}

private fun chooseOptimalSize(
    choices: Array<Size>, textureViewWidth: Int, textureViewHeight: Int,
    maxWidth: Int, maxHeight: Int, aspectRatio: Size
): Size {
    // Collect the supported resolutions that are at least as big as the preview Surface
    val bigEnough: MutableList<Size> = ArrayList()
    // Collect the supported resolutions that are smaller than the preview Surface
    val notBigEnough: MutableList<Size> = ArrayList()
    val w = aspectRatio.width
    val h = aspectRatio.height
    for (option in choices) {
        if (option.width <= maxWidth && option.height <= maxHeight && option.height == option.width * h / w) {
            if (option.width >= textureViewWidth &&
                option.height >= textureViewHeight
            ) {
                bigEnough.add(option)
            } else {
                notBigEnough.add(option)
            }
        }
    }

    // Pick the smallest of those big enough. If there is no one big enough, pick the
    // largest of those not big enough.
    return when {
        bigEnough.size > 0 -> {
            Collections.min(bigEnough, CompareSizesByArea())
        }
        notBigEnough.size > 0 -> {
            Collections.max(notBigEnough, CompareSizesByArea())
        }
        else -> {
            choices[0]
        }
    }
}

fun Context.calculateTime(hour: Int, minute: Int = 0): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.timeInMillis
    val time = calendar.timeInMillis
    val now = System.currentTimeMillis()
    return  if (time > now) {
        time - now
    } else {
        time + (24 * 60 * 60 * 1000) - now
    }
}
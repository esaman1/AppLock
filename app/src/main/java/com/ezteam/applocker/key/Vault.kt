package com.ezteam.applocker.key

import android.util.Base64
import com.ezteam.applocker.utils.CryptoUtil

object Vault {
    const val KEY_BACK = "KEY_BACK"
    const val KEY_FILE_NAME_IMAGE = "KEY_FILE_NAME_IMAGE"
    const val KEY_FILE_NAME_VIDEO = "KEY_FILE_NAME_VIDEO"
    const val KEY_IMAGE_NAME = "KEY_IMAGE_NAME"
    const val KEY_IMAGE_PATH = "KEY_IMAGE_PATH"
    const val KEY_VIDEO_DECRYPT_PATH= "KEY_VIDEO_DECRYPT_PATH"
    const val KEY_VIDEO_THUMBNAIL_PATH= "KEY_VIDEO_THUMBNAIL_PATH"
    const val IS_HIDE = "IS_HIDE"
    const val REQUEST_CODE_DELETE_IMAGE = 1
    const val REQUEST_CODE_DELETE_VIDEO = 2
    fun PW(): String {
        return Base64.encodeToString(CryptoUtil.hash("EZ", "EZSTUDIO"), Base64.DEFAULT)
    }
}
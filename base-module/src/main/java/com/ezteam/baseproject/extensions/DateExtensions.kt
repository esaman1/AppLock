package com.ezteam.baseproject.extensions

import com.ezteam.baseproject.utils.DateUtils
import java.util.*

fun Date.timeLast(): String {
    val interval = System.currentTimeMillis() - time
    return when {
        interval/1000 > 86400 -> {
            DateUtils.longToDateString(interval, "d'days' hh'h' mm 'mins'")
        }
        interval/1000 > 3600 -> {
            DateUtils.longToDateString(interval, "hh'h' mm 'mins'")
        }
        interval/1000 > 60 -> {
            DateUtils.longToDateString(interval, "mm 'mins'")
        }
        interval/1000 > 0 -> {
            DateUtils.longToDateString(interval, "mm 'seconds'")
        }
        else -> {
            ""
        }
    }
}
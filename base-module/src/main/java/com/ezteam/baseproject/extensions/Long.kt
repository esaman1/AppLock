package com.ezteam.baseproject.extensions

import android.content.Context
import com.ezteam.baseproject.R
import java.text.SimpleDateFormat
import java.util.*

fun Long?.orZero(): Long {
    return this ?: 0L
}

fun Long?.toDate(formatString: String): String? {
    val format = SimpleDateFormat(formatString, Locale.US)
    format.timeZone = TimeZone.getTimeZone("UTC");
    return format.formatOrNull(this)
}

fun Long.distanceTime(context: Context): String? {
    val currentTime = System.currentTimeMillis()
    val dicstance = (currentTime - this) / 1000
    return when {
        dicstance < 60 -> {
            context.getString(R.string.just_now_time)
        }
        dicstance < 60 * 60 -> {
            val minute = (dicstance / 60).toInt()
            context.getString(R.string.minute_ago, minute.toString())
        }
        dicstance < 60 * 60 * 24 -> {
            val minute = (dicstance / (60 * 60)).toInt()
            context.getString(R.string.hour_ago, minute.toString())
        }
        dicstance < 60 * 60 * 24 * 30 -> {
            val minute = (dicstance / (24 * 60 * 60)).toInt()
            context.getString(R.string.day_ago, minute.toString())
        }
        dicstance < 60 * 60 * 24 * 30 * 12 -> {
            val simpleDateFormat = SimpleDateFormat("dd MMM")
            simpleDateFormat.format(Date(this))
        }
        else -> {
            val simpleDateFormat = SimpleDateFormat("dd MMM yyyy")
            simpleDateFormat.format(Date(this))
        }
    }
}


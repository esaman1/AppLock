package com.ezteam.baseproject.extensions

import android.text.TextUtils

fun String.getParams(prefix: String): String {
    if (isEmpty() && !contains("@")) {
        return ""
    }

    val values = split("@").find {
        it.contains(prefix)
    }
    values?.let {
        return values.replace(prefix, "").trim()
    } ?: kotlin.run {
        return ""
    }
}

fun String.capitalize(): String {
    if (TextUtils.isEmpty(this)) {
        return this
    }
    val arr = this.toCharArray()
    var capitalizeNext = true
    var phrase = ""
    for (c in arr) {
        if (capitalizeNext && Character.isLetter(c)) {
            phrase += Character.toUpperCase(c)
            capitalizeNext = false
            continue
        } else if (Character.isWhitespace(c)) {
            capitalizeNext = true
        }
        phrase += c
    }
    return phrase
}

fun String.capitalizeFirst(): String {
    return when {
        this.length > 1 -> {
            this[0].uppercase() + this.substring(1)
        }
        this.isNotEmpty() -> {
            this[0].uppercase()
        }
        else -> {
            ""
        }
    }
}
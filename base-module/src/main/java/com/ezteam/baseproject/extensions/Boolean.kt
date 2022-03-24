package com.ezteam.baseproject.extensions

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}
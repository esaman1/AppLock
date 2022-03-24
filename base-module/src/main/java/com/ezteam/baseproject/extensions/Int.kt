package com.ezteam.baseproject.extensions

fun Int?.orZero(): Int {
    return this ?: 0
}
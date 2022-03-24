package com.ezteam.baseproject.extensions

fun Double?.orZero(): Double {
    return this ?: 0.0
}
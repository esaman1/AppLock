package com.ezteam.applocker.item

data class ItemImageRestore(
    val path: String, var lastModified: Long, var size: Long, var number: Int = 0,
    var isSelected: Boolean = false
)
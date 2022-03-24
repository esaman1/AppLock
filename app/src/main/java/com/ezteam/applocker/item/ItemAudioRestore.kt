package com.ezteam.applocker.item

class ItemAudioRestore(
    val path: String,
    val lastModified: Long,
    var size: Long,
    var duration : Long,
    var number: Int = 0,
    var isSelected: Boolean = false
)
package com.ezteam.applocker.item

data class ItemVideoRestore(
    val path: String, val lastModified: Long, var size: Long,var type : String , var duration : Long ,var number: Int = 0,
    var isSelected: Boolean = false
)
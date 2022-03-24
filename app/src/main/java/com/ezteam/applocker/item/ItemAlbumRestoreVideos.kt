package com.ezteam.applocker.item

data class ItemAlbumRestoreVideos (val path : String, val lastModified : Long,  val listVideos: MutableList<ItemVideoRestore>)
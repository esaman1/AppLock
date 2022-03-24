package com.ezteam.applocker.item

data class ItemAlbumRestoreImages(val path : String , val lastModified : Long  , val listPhoto  : MutableList<ItemImageRestore>)
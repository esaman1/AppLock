package com.ezteam.applocker.item

import android.graphics.Bitmap
import java.io.File

data class  ItemDetailPhoto(val file: File, var number: Int  = 0 ,var isSelected: Boolean = false)
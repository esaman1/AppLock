package com.ezteam.applocker.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.ezteam.applocker.item.ItemNumber
import com.ezteam.applocker.key.KeyTheme
import com.ezteam.baseproject.utils.PreferencesUtils
import java.io.IOException
import java.io.InputStream


object ThemeUtils {
    private const val normal = "normal"
    private const val down = "down"
    fun themeBackground(context: Context): Drawable? {
        val folder = PreferencesUtils.getString(KeyTheme.KEY_BACKGROUND, "default")
        return getImageTheme(context, folder, "background")
    }

    fun themeDotPatternNormal(context: Context): Drawable? {
        return getImageTheme(context, getFolderNameTheme(), "dot_pattern_normal")
    }

    fun themeDotPatternHighlight(context: Context): Drawable? {
        return getImageTheme(context, getFolderNameTheme(), "dot_pattern_highlight")
    }

    fun themeDotPatternError(context: Context): Drawable? {
        return getImageTheme(context, getFolderNameTheme(), "dot_pattern_error")
    }

    fun listThemeNumber(context: Context): MutableList<ItemNumber> {
        return getListNumber(context, getFolderNameTheme())
    }

    private fun getListNumber(context: Context, folder: String): MutableList<ItemNumber> {
        val list = mutableListOf<ItemNumber>()
        for (i in 1..9) {
            list.add(
                ItemNumber(
                    i,
                    getImageNumberTheme(context, folder, i, normal),
                    getImageNumberTheme(context, folder, i, down)
                )
            )
        }
        list.add(ItemNumber(-2))
        list.add(
            ItemNumber(
                0,
                getImageNumberTheme(context, folder, 0, normal),
                getImageNumberTheme(context, folder, 0, down)
            )
        )
        list.add(
            ItemNumber(
                -1,
                getImageNumberTheme(context, folder, -1, normal, true),
                getImageNumberTheme(context, folder, -1, down, true)
            )
        )
        return list
    }

    fun getFolderNameTheme(): String {
        return (PreferencesUtils.getString(KeyTheme.KEY_THEME, "default"))

    }

    private fun getImageNumberTheme(
        context: Context,
        folder: String,
        number: Int,
        stateImage: String,
        isDelete: Boolean = false

    ): Drawable? {
        return try {
            if (isDelete) {
                val ims: InputStream =
                    context.assets.open("theme/$folder/${stateImage}_delete.webp")
                // load image as Drawable
                BitmapDrawable(context.resources, ims)
            } else {
                val ims: InputStream =
                    context.assets.open("theme/$folder/${stateImage}_$number.webp")
                // load image as Drawable
                BitmapDrawable(context.resources, ims)
            }
        } catch (e: IOException) {
            null
        }
    }

    fun getImageTheme(context: Context, folder: String, nameImage: String): Drawable? {
        return try {
            val ims = context.assets.open("theme/$folder/$nameImage.webp")
            BitmapDrawable(context.resources, ims)
        } catch (e: IOException) {
            null
        } catch (o: OutOfMemoryError) {
            null
        }
    }

    fun getFolderName(context: Context): MutableList<String> {
        context.assets.list("theme")?.let {
            return it.toMutableList()
        }
        return mutableListOf()
    }

    fun getLineColor(): Int {
        val theme = PreferencesUtils.getString(KeyTheme.KEY_BACKGROUND, "default")
        when (theme) {
            "flower", "halloween", "egg", "emoji", "fruit", "chirstmas", "game", "balloon", "chicken" -> {
                return Color.parseColor("#7C1E22")
            }
            "blur" -> return Color.parseColor("#4DFFFFFF")

            else -> {
                return Color.parseColor("#4DFFFFFF")
            }
        }
    }

    fun getLineColorText(): Int {
        val theme = PreferencesUtils.getString(KeyTheme.KEY_BACKGROUND, "default")
        when (theme) {
            "flower", "halloween", "egg", "emoji", "fruit", "chirstmas", "game", "balloon", "chicken" -> {
                return Color.parseColor("#363636")
            }
            else -> {
                return Color.parseColor("#FFFFFF")
            }
        }
    }
}
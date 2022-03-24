package com.ezteam.applocker.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * ------------------------------------
 *
 *
 * Author: Yongjie Zhuang
 *
 *
 * ------------------------------------
 *
 *
 * Util class for Date related operations
 *
 */
object DateUtil {
    /**
     * Create a string of Date in this format: ddMMyyyy
     *
     * @return a string of Date in this format: ddMMyyyy
     */
    val dateStr: String
        get() = SimpleDateFormat("ddMMyyyy").format(Date())

    /**
     * Convert date to a string in this format: dd-MM-yyyy
     *
     * @return a string of Date in this format: dd-MM-yyyy
     */
    fun toDateStr(date: Date?): String {
        return SimpleDateFormat("dd-MM-yyyy").format(date)
    }
}
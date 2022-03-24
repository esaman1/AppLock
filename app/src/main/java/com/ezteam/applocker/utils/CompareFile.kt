package com.ezteam.applocker.utils

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.*

object CompareFile {
    @Throws(IOException::class)
    fun imagesAreEqual(file1: File, file2: File): Boolean {
        if (!file1.exists()) {
            return false
        }
        if (!file2.exists()) {
            return false
        }
        if (file1.length()!=file2.length()) {
            return false
        }
        if (file1.length() <= 3000) {
            return try {
                FileUtils.contentEquals(file1, file2)
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
        try {
            if (file1.exists()) {
                val input1: InputStream = FileInputStream(file1)
                val input2: InputStream = FileInputStream(file2)
                try {
                    val startBufferFile1 = ByteArray(512)
                    IOUtils.read(input1, startBufferFile1, 0, 512)
                    var str = String(startBufferFile1)
                    IOUtils.skip(input1, file1.length() / 2 - 256)
                    val midBufferFile1 = ByteArray(512)
                    IOUtils.read(input1, midBufferFile1, 0, 512)
                    val midTextFile1 = String(midBufferFile1)
                    IOUtils.skip(input1, file1.length() - 512)
                    val endBufferFile1 = ByteArray(512)
                    IOUtils.read(input1, endBufferFile1, 0, 512)
                    val endTextFile1 = String(endBufferFile1)
                    val startBufferFile2 = ByteArray(512)
                    IOUtils.read(input2, startBufferFile2, 0, 512)
                    str = String(startBufferFile2)
                    IOUtils.skip(input2, file2.length() / 2 - 256)
                    val midBufferFile2 = ByteArray(512)
                    IOUtils.read(input2, midBufferFile2, 0, 512)
                    str = String(midBufferFile2)
                    IOUtils.skip(input2, file2.length() - 512)
                    val endBufferFile2 = ByteArray(512)
                    IOUtils.read(input2, endBufferFile2, 0, 512)
                    val endTextFile2 = String(endBufferFile2)
                    if (str==str&&midTextFile1==str&&endTextFile1==endTextFile2) {
                        return true
                    }
                    input1.close()
                    input2.close()
                    input1.close()
                    input2.close()
                    return false
                } catch (e2: IOException) {
                } finally {
                    input1.close()
                    input2.close()
                }
            }
        } catch (e3: FileNotFoundException) {
        }
        return false
    }

//    fun contentEquals(listAllImage: MutableList<File>): MutableList<MutableList<File>> {
//        val arrResult = mutableListOf<MutableList<File>>()
//        listAllImage.sortWith { f1, f2 ->
//            when {
//                f1.length() > f2.length() -> {
//                    1
//                }
//                f1.length() < f2.length() -> {
//                    -1
//                }
//                else -> {
//                    0
//                }
//            }
//        }
//        var listSetImage = mutableListOf<File>()
//        var isSameLastImage = false
//        for (i in 0 until listAllImage.size) {
//            isSameLastImage =
//                if (i!=listAllImage.size - 1&&imagesAreEqual(listAllImage[i], listAllImage[i + 1])
//                ) {
//                    listSetImage.add(listAllImage[i])
//                    true
//                } else {
//                    if (isSameLastImage) {
//                        listSetImage.add(listAllImage[i])
//                    }
//                    if(listSetImage.isNotEmpty()){
//                        arrResult.add(listSetImage)
//                        listSetImage = mutableListOf()
//                    }
//                    false
//                }
//        }
//        return arrResult
//    }
}
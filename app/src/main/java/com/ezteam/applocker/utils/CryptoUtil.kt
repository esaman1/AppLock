package com.ezteam.applocker.utils

import android.os.Environment
import java.io.IOException
import java.io.RandomAccessFile
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


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
 * Util Class for encryption, decryption, hashing, etc.
 *
 */
object CryptoUtil {
    /*
     * Specification for encryption and decryption
     */
    private const val HASHING_ALGORITHM = "SHA-256"
    private const val ENCRYPTION_STANDARD = "AES"
    private const val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private const val SALT_LEN = 4
    private val filenamePlain =
        Environment.getExternalStorageDirectory().absolutePath + "/plain.dat"

    //    private val filenameEncrypt =
//        Environment.getExternalStorageDirectory().absolutePath + "/encrypt.txt"
    private val filenameDecrypt =
        Environment.getExternalStorageDirectory().absolutePath + "/decrypt.dat"

    /**
     * Hash text with salt
     *
     * @param str  text
     * @param salt salt
     * @return hash in `byte[]` or `NULL` if error happens
     */
    fun hash(str: String, salt: String): ByteArray? {
        return try {
            // append salt to password
            val cred = str + salt
            val credBytes = cred.toByteArray(charset("UTF-8"))

            // digest the credBytes
            val md =
                MessageDigest.getInstance(HASHING_ALGORITHM)
            md.update(credBytes, 0, credBytes.size)
            md.digest()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generate random salt
     *
     * @param len length of salt
     * @return salt
     */
    private fun randSalt(len: Int): String {
        val sr = SecureRandom()
        val sb = StringBuilder(len)
        for (i in 0 until len) {
            sb.append(CHARS[sr.nextInt(CHARS.length)])
        }
        return sb.toString()
    }

    /**
     * Generate random salt of the default length
     *
     * @return salt
     * @see {@link CryptoUtil.SALT_LEN}
     */
    fun randSalt(): String {
        return randSalt(SALT_LEN)
    }

    /**
     * create the a `SecretKeySpec` for the specified
     * `ENCRYPTION_STANDARD` through hashing given password using the
     * specified `HASHING_ALGORITHM`
     *
     * @param pw password
     * @return SecretKeySpec
     * @see {@link CryptoUtil.HASHING_ALGORITHM}
     *
     * @see {@link CryptoUtil.ENCRYPTION_STANDARD}
     */
    private fun createKey(pw: String): SecretKeySpec? {
        return try {
            val pwBytes = pw.toByteArray(charset("UTF-8"))

            // Create secret Key factory based on the specified algorithm
            val md = MessageDigest.getInstance(HASHING_ALGORITHM)

            // digest the pwBytes to be a new key
            md.update(pwBytes, 0, pwBytes.size)
            val key = md.digest()
            SecretKeySpec(key, ENCRYPTION_STANDARD)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Decrypt using specified `ENCRYPTION_STANDARD`
     *
     * @param data encryptedData
     * @param pw   password
     * @return original data byte[]
     * @see {@link CryptoUtil.ENCRYPTION_STANDARD}
     */
    fun decrypt(data: ByteArray?, pw: String, p: String): ByteArray? {
        val keySpec = createKey(pw)
        return try {
            val cipher =
                Cipher.getInstance(ENCRYPTION_STANDARD)
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            cipher.doFinal(data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } catch (m: OutOfMemoryError) {
            m.printStackTrace()
            null
        }
    }

    /**
     * Encrypt using specified `ENCRYPTION_STANDARD`
     *
     * @param data image data
     * @param pw   password
     * @return encrypted byte[]
     */
    fun encrypt(data: ByteArray?, pw: String, p: String): ByteArray? {
        val keySpec = createKey(pw)
        return try {
            val cipher =
                Cipher.getInstance(ENCRYPTION_STANDARD)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            cipher.doFinal(data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } catch (m: OutOfMemoryError) {
            m.printStackTrace()
            null
        }
    }

    // just for creating a large file within seconds
    @Throws(IOException::class)
    private fun createFileWithDefinedLength(filenameString: String, sizeLong: Long) {
        val raf = RandomAccessFile(filenameString, "rw")
        raf.use { raf ->
            raf.setLength(sizeLong)
        }
    }
}
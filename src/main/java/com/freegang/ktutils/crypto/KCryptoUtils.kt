package com.freegang.ktutils.crypto

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object KCryptoUtils {
    private val _iv = "abcdefghijklmnop" // This is an example. Use a secure random IV in production!

    @JvmStatic
    fun md5(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        return bytes.toHex()
    }

    @JvmStatic
    fun sha1(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
        return bytes.toHex()
    }

    @JvmStatic
    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.toHex()
    }

    @JvmStatic
    @JvmOverloads
    fun aesEncrypt(
        input: String,
        key: String,
        iv: String = _iv,
    ): String {
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keyBytes = MessageDigest.getInstance("SHA-256").digest(key.toByteArray())
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")
        val ivParameterSpec = IvParameterSpec(iv.toByteArray())
        c.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encoded = c.doFinal(input.toByteArray())
        return Base64.encodeToString(encoded, Base64.DEFAULT)
    }

    @JvmStatic
    @JvmOverloads
    fun aesDecrypt(
        input: String,
        key: String,
        iv: String = _iv,
    ): String {
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keyBytes = MessageDigest.getInstance("SHA-256").digest(key.toByteArray())
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")
        val ivParameterSpec = IvParameterSpec(iv.toByteArray())
        c.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val decoded = Base64.decode(input, Base64.DEFAULT)
        return String(c.doFinal(decoded))
    }

    @JvmStatic
    private fun ByteArray.toHex(): String {
        return joinToString("") { "%02x".format(it) }
    }
}

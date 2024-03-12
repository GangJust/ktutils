package com.freegang.ktutils.net

import android.os.Build
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

object KHttpUtils {
    val ANDROUD_UA get() = System.getProperty("http.agent")

    @Throws(IOException::class)
    private fun commonConnection(url: URL): HttpURLConnection {
        val connect: HttpURLConnection = url.openConnection() as HttpURLConnection
        connect.requestMethod = "GET"
        connect.connectTimeout = 5000
        connect.readTimeout = 5000
        connect.instanceFollowRedirects = true
        connect.useCaches = false
        connect.doInput = true
        connect.setRequestProperty("User-Agent", ANDROUD_UA)
        connect.setRequestProperty("Accept", "*/*")
        connect.setRequestProperty("Accept-Encoding", "identity");
        // 如果是 HTTPS 请求，信任所有证书
        if (connect is HttpsURLConnection) {
            trustAllHosts()
        }
        return connect
    }

    /**
     * GET请求
     *
     * @param sourceUrl 目标URL地址
     * @param params    参数
     * @return 文本内容
     */
    @JvmStatic
    fun get(sourceUrl: String, params: String = ""): String {
        val sourceUrl = if (params.isBlank()) {
            sourceUrl
        } else {
            if (sourceUrl.contains("?")) "$sourceUrl&$params" else "$sourceUrl?$params"
        }

        var body = ""
        var connect: HttpURLConnection? = null
        try {
            connect = commonConnection(URL(sourceUrl))
            val inputStream =
                if (connect.responseCode == HttpURLConnection.HTTP_OK || connect.responseCode == HttpURLConnection.HTTP_CREATED) {
                    InputStreamReader(connect.inputStream, StandardCharsets.UTF_8)
                } else {
                    InputStreamReader(connect.errorStream, StandardCharsets.UTF_8)
                }
            body = inputStream.readText()
            inputStream.close()
            connect.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                connect?.disconnect()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return body
    }

    /**
     * 下载文件
     * @param sourceUrl 目标URL地址
     * @param output 输出流
     * @param listener 下载监听器
     */
    @JvmStatic
    fun download(sourceUrl: String, output: OutputStream, listener: DownloadListener? = null): Boolean {
        var connect: HttpURLConnection? = null
        var total = 0L
        var realCount = 0L
        try {
            connect = commonConnection(URL(sourceUrl))
            total = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connect.contentLengthLong
            } else {
                connect.contentLength.toLong()
            }
            val input = connect.inputStream
            input.use {
                val buffer = ByteArray(4096)
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    output.write(buffer, 0, count)
                    realCount += count
                    listener?.downloading(realCount, total, null)
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            listener?.downloading(realCount, total, e)
        } finally {
            try {
                output.flush()
                output.close()
                connect?.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                listener?.downloading(realCount, total, e)
            }
        }
        return false
    }

    fun interface DownloadListener {
        fun downloading(real: Long, total: Long, e: Throwable?)
    }

    /**
     * 信任所有证书
     */
    private fun trustAllHosts() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf<TrustManager>(TrustAllCerts()), null)
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
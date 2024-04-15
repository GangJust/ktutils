package com.freegang.ktutils.net

import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import okio.buffer
import okio.sink
import java.io.File
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

object KOkHttpUtils {
    const val RESULT_NOTHING = "Nothing"

    @JvmStatic
    val client: OkHttpClient by lazy {
        // 创建 OkHttpClient.Builder 实例
        val builder = OkHttpClient.Builder()
        // 设置连接超时时间
        builder.connectTimeout(30, TimeUnit.SECONDS)
        // 设置读取超时时间
        builder.readTimeout(30, TimeUnit.SECONDS)
        // 设置写入超时时间
        builder.writeTimeout(30, TimeUnit.SECONDS)
        // 添加自定义的证书验证(默认信任所有证书, 请酌情修改)
        val trustManager = TrustAllCerts()
        builder.sslSocketFactory(createSSLSocketFactory(trustManager), trustManager)
        // 构建 OkHttpClient 实例
        builder.build()
    }

    /**
     * 创建自定义的 SSLSocketFactory
     * @param trustManager 自定义的信任管理器
     * @return SSLSocketFactory
     */
    private fun createSSLSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(trustManager), SecureRandom())
        return sslContext.socketFactory
    }

    /**
     * 发送一个 GET 请求，并返回响应的字符串结果
     * @param url 请求的 URL
     * @return 响应的字符串结果，如果请求失败则返回空字符串
     */
    @JvmStatic
    @JvmOverloads
    fun get(
        url: String,
        headers: Headers = Headers.headersOf(),
    ): String {
        val request = Request.Builder()
            .headers(headers)
            .url(url)
            .build()

        return try {
            client.newCall(request).execute().body?.string() ?: RESULT_NOTHING
        } catch (e: Exception) {
            e.printStackTrace()
            e.message ?: RESULT_NOTHING
        }
    }

    /**
     * 发送一个 GET 请求，并返回响应的字符串结果
     * @param url 请求的 URL
     * @param params 请求的参数，以 Map 的形式传递
     * @return 响应的字符串结果，如果请求失败则返回空字符串
     */
    @JvmStatic
    @JvmOverloads
    fun getMap(
        url: String,
        params: Map<String, String>,
        headers: Headers = Headers.headersOf(),
    ): String {
        var parameters = ""
        for ((key, value) in params) {
            parameters = "${parameters}&${key}=${value}"
        }
        parameters = parameters.removePrefix("&").removeSuffix("&")
        return if (url.contains("?")) {
            get("${url}&${parameters}", headers)
        } else {
            get("${url}?${parameters}", headers)
        }
    }

    /**
     * 发送一个 POST 请求，并返回响应的字符串结果
     * @param url 请求的 URL
     * @param body 请求的请求体
     * @return 响应的字符串结果，如果请求失败则返回空字符串
     */
    @JvmStatic
    @JvmOverloads
    fun post(
        url: String,
        body: RequestBody,
        headers: Headers = Headers.headersOf(),
    ): String {
        val request = Request.Builder()
            .headers(headers)
            .post(body)
            .url(url)
            .build()

        return try {
            client.newCall(request).execute().body?.string() ?: RESULT_NOTHING
        } catch (e: Exception) {
            e.printStackTrace()
            e.message ?: RESULT_NOTHING
        }
    }

    /**
     * 发送一个 POST 请求，并返回响应的字符串结果
     * @param url 请求的 URL
     * @param params 请求的参数，以 Map 的形式传递
     * @return 响应的字符串结果，如果请求失败则返回空字符串
     */
    @JvmStatic
    @JvmOverloads
    fun postMap(
        url: String,
        params: Map<String, String>,
        headers: Headers = Headers.headersOf(),
    ): String {
        val formBodyBuilder = FormBody.Builder()
        for ((key, value) in params) {
            formBodyBuilder.add(key, value)
        }
        val formBody = formBodyBuilder.build()
        return post(url, formBody, headers)
    }

    /**
     * 发送一个 POST 请求，并返回响应的字符串结果
     * @param url 请求的 URL
     * @param json 请求的 JSON 数据
     * @return 响应的字符串结果，如果请求失败则返回空字符串
     */
    @JvmStatic
    @JvmOverloads
    fun postJson(
        url: String,
        json: String,
        headers: Headers = Headers.headersOf(),
    ): String {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = json.toRequestBody(mediaType)
        return post(url, requestBody, headers)
    }

    /**
     * 发送一个 PUT 请求，并返回响应的字符串结果
     * @param url 请求的 URL
     * @param body 请求的请求体
     * @return 响应的字符串结果，如果请求失败则返回空字符串
     */
    @JvmStatic
    @JvmOverloads
    fun put(
        url: String,
        body: RequestBody,
        headers: Headers = Headers.headersOf(),
    ): String {
        val request = Request.Builder()
            .headers(headers)
            .put(body)
            .url(url)
            .build()

        return try {
            client.newCall(request).execute().body?.string() ?: RESULT_NOTHING
        } catch (e: Exception) {
            e.printStackTrace()
            e.message ?: RESULT_NOTHING
        }
    }

    /**
     * 发送一个 PUT 请求，并返回响应的字符串结果
     * @param url 请求的 URL
     * @param params 请求的参数，以 Map 的形式传递
     * @return 响应的字符串结果，如果请求失败则返回空字符串
     */
    @JvmStatic
    @JvmOverloads
    fun putMap(
        url: String,
        params: Map<String, String>,
        headers: Headers = Headers.headersOf(),
    ): String {
        val formBodyBuilder = FormBody.Builder()
        for ((key, value) in params) {
            formBodyBuilder.add(key, value)
        }
        val formBody = formBodyBuilder.build()
        return put(url, formBody, headers)
    }

    /**
     * 发送一个 PUT 请求，并返回响应的字符串结果
     * @param url 请求的 URL
     * @param json 请求的 JSON 数据
     * @return 响应的字符串结果，如果请求失败则返回空字符串
     */
    @JvmStatic
    @JvmOverloads
    fun putJson(
        url: String,
        json: String,
        headers: Headers = Headers.headersOf(),
    ): String {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = json.toRequestBody(mediaType)
        return put(url, requestBody, headers)
    }

    /**
     * 发送一个请求，并返回响应的字符串结果
     * @param url 请求的 URL
     * @param requestBody 请求体
     * @return 响应的字符串结果，如果请求失败则返回空字符串
     */
    @JvmStatic
    @JvmOverloads
    fun request(
        url: String,
        method: String,
        requestBody: RequestBody? = null,
        headers: Headers? = null,
    ): String {
        val request = Request.Builder()
            .headers(headers ?: Headers.headersOf())
            .method(method, requestBody)
            .url(url)
            .build()

        return try {
            client.newCall(request).execute().body?.string() ?: RESULT_NOTHING
        } catch (e: Exception) {
            e.printStackTrace()
            e.message ?: RESULT_NOTHING
        }
    }

    /**
     * 创建并连接WebSocket
     * @param url WebSocket的URL地址
     * @param l WebSocket的监听器
     * @return 创建的WebSocket实例
     */
    @JvmStatic
    fun websocket(
        url: String,
        l: WebSocketListener,
    ): WebSocket {
        // 创建Request对象
        val request = Request.Builder()
            .url(url)
            .build()
        // 使用client创建并连接WebSocket
        return client.newWebSocket(request, l)
    }

    /**
     * 上传文件
     * @param url 上传的目标 URL
     * @param name 文件参数字段名称
     * @param file 要上传的文件
     * @param params 额外的参数，以 Map 的形式传递，可选参数
     * @param progressListener 上传进度回调监听, 可选参数
     * @return 响应的字符串结果，如果请求失败则返回空字符串
     */
    @JvmStatic
    @JvmOverloads
    fun uploadFile(
        url: String,
        name: String,
        file: File,
        params: Map<String, String> = emptyMap(),
        headers: Headers = Headers.headersOf(),
        progressListener: UploadProgressListener? = null,
    ): String {
        MultipartBody.Builder().setType(MultipartBody.FORM)
        val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val requestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val fileBody = CountingRequestBody(requestBody, file.name, progressListener)
        multipartBuilder.addFormDataPart(name, file.name, fileBody)
        for ((key, value) in params) {
            multipartBuilder.addFormDataPart(key, value)
        }
        val multipartBody = multipartBuilder.build()

        val request = Request.Builder()
            .headers(headers)
            .post(multipartBody)
            .url(url)
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.body?.string() ?: RESULT_NOTHING
        } catch (e: Exception) {
            e.printStackTrace()
            RESULT_NOTHING
        }
    }

    /**
     * 下载文件
     * @param url 下载文件的 URL
     * @param outFile 输出的文件
     * @param progressListener 下载进度回调监听器
     * @return true 表示下载成功，false 表示下载失败
     */
    @JvmStatic
    @JvmOverloads
    fun downloadFile(
        url: String,
        outFile: File,
        headers: Headers = Headers.headersOf(),
        progressListener: DownloadProgressListener? = null
    ): Boolean {
        val request = Request.Builder()
            .headers(headers)
            .url(url)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body
                if (responseBody != null) {
                    val contentLength = responseBody.contentLength()
                    val source = responseBody.source()
                    val bufferedSink = outFile.sink().buffer()
                    var bytesRead: Long = 0

                    while (true) {
                        val readCount =
                            source.read(bufferedSink.buffer, DEFAULT_BUFFER_SIZE.toLong())
                        if (readCount == -1L) break

                        bytesRead += readCount
                        bufferedSink.emitCompleteSegments()

                        progressListener?.onProgress(
                            bytesRead,
                            contentLength,
                            bytesRead == contentLength
                        )
                    }

                    bufferedSink.flush()
                    bufferedSink.close()

                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 自定义的 RequestBody，用于添加上传进度回调
     * @param delegate 原始的 RequestBody
     * @param progressListener 上传进度回调接口
     */
    private class CountingRequestBody(
        private val delegate: RequestBody,
        private val filename: String,
        private val progressListener: UploadProgressListener? = null,
    ) : RequestBody() {
        override fun contentType() = delegate.contentType()

        override fun contentLength() = delegate.contentLength()

        override fun writeTo(sink: BufferedSink) {
            val countingSink = CountingSink(sink)
            val bufferedSink = countingSink.buffer()

            delegate.writeTo(bufferedSink)

            bufferedSink.flush()
        }

        private inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {
            private var bytesWritten = 0L

            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                bytesWritten += byteCount
                progressListener?.onProgress(
                    filename,
                    bytesWritten,
                    contentLength(),
                    bytesWritten == contentLength()
                )
            }
        }
    }

    /**
     * 上传进度回调接口
     */
    fun interface UploadProgressListener {
        /**
         * 上传进度回调方法
         * @param filename 当前上传的文件名
         * @param bytesWritten 已上传的字节数
         * @param contentLength 总字节长度
         * @param done 是否上传完成
         */
        fun onProgress(filename: String, bytesWritten: Long, contentLength: Long, done: Boolean)
    }

    /**
     * 下载进度回调接口
     */
    fun interface DownloadProgressListener {
        /**
         * 下载进度回调方法
         * @param bytesRead 已下载的字节数
         * @param contentLength 总字节长度
         * @param done 是否下载完成
         */
        fun onProgress(bytesRead: Long, contentLength: Long, done: Boolean)
    }
}



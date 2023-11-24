package com.freegang.ktutils.net

import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder

// android.net.Uri 获取url带锚点#的参数为null, 故让gpt生成了一个工具类
// "https://www.example.com:8080/#123?a=234&b=456"
object KUrlUtils {
    /**
     * 获取 URL 的协议部分。
     *
     * @param url 要解析的 URL 字符串
     * @return URL 的协议部分，如果解析失败或协议不存在，则返回空字符串
     */
    @JvmStatic
    fun getProtocol(url: String): String {
        return try {
            URL(url).protocol ?: ""
        } catch (e: MalformedURLException) {
            ""
        }
    }

    /**
     * 获取 URL 的域名部分。
     *
     * @param url 要解析的 URL 字符串
     * @return URL 的域名部分，如果解析失败或域名不存在，则返回空字符串
     */
    @JvmStatic
    fun getDomain(url: String): String {
        return try {
            URL(url).host ?: ""
        } catch (e: MalformedURLException) {
            ""
        }
    }

    /**
     * 获取 URL 的域名部分。
     *
     * @param url 要解析的 URL 字符串
     * @return URL 的域名部分，如果解析失败或域名不存在，则返回空字符串
     */
    @JvmStatic
    fun getHost(url: String): String {
        return getDomain(url)
    }

    /**
     * 获取 URL 的端口部分。
     *
     * @param url 要解析的 URL 字符串
     * @return URL 的端口部分，如果解析失败或端口不存在，则返回 -1
     */
    @JvmStatic
    fun getPort(url: String): Int {
        return try {
            URL(url).port
        } catch (e: MalformedURLException) {
            0
        }
    }

    /**
     * 获取 URL 的路径部分。
     *
     * @param url 要解析的 URL 字符串
     * @return URL 的路径部分，如果解析失败或路径不存在，则返回空字符串
     */
    @JvmStatic
    fun getPath(url: String): String {
        return try {
            URL(url).path ?: ""
        } catch (e: MalformedURLException) {
            ""
        }
    }

    /**
     * 获取 URL 的锚点部分。
     *
     * @param url 要解析的 URL 字符串
     * @return URL 的锚点部分，如果解析失败或锚点不存在，则返回空字符串
     */
    @JvmStatic
    fun getFragment(url: String): String {
        return try {
            URL(url).ref ?: ""
        } catch (e: MalformedURLException) {
            ""
        }
    }

    /**
     * 获取 URL 的所有参数，并返回一个键值对的 Map。
     *
     * @param url 要解析的 URL 字符串
     * @return 包含 URL 参数的键值对 Map，如果 URL 不符合规范或解析失败，则返回空的 Map
     */
    @JvmStatic
    fun getQueryParameters(url: String): Map<String, String> {
        return try {
            val query = URL(url).query ?: ""
            val finalQuery = query.ifEmpty { URL(url).ref?.substringAfter("?", "") ?: "" }
            val parameters = mutableMapOf<String, String>()

            if (finalQuery.isNotEmpty()) {
                val pairs = finalQuery.split("&")

                for (pair in pairs) {
                    val idx = pair.indexOf("=")
                    val key = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                    val value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")

                    parameters[key] = value
                }
            }

            parameters
        } catch (e: MalformedURLException) {
            emptyMap()
        }
    }

    /**
     * 获取 URL 中指定键的参数值列表。
     *
     * @param url 要解析的 URL 字符串
     * @param key 要提取参数值的键
     * @return 包含指定键的参数值列表，如果参数不存在或解析失败，则返回空列表
     */
    @JvmStatic
    fun getQueryParameters(url: String, key: String): List<String> {
        return try {
            val query = URL(url).query ?: ""
            val finalQuery = query.ifEmpty { URL(url).ref?.substringAfter("?", "") ?: "" }
            val parameters = mutableListOf<String>()

            if (finalQuery.isNotEmpty()) {
                val pairs = query.split("&")

                for (pair in pairs) {
                    val idx = pair.indexOf("=")
                    if (idx >= 0) {
                        val parameterKey = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                        val parameterValue = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")

                        if (parameterKey == key) {
                            parameters.add(parameterValue)
                        }
                    }
                }
            }

            return parameters
        } catch (e: MalformedURLException) {
            emptyList()
        }
    }

    /**
     * 获取 URL 中指定键的参数值。
     *
     * @param url 要解析的 URL 字符串
     * @param key 要提取参数值的键
     * @return 参数值，如果参数不存在或解析失败，则返回空字符串
     */
    @JvmStatic
    fun getQueryParameter(url: String, key: String): String {
        return try {
            val query = URL(url).query ?: ""
            val finalQuery = query.ifEmpty { URL(url).ref?.substringAfter("?", "") ?: "" }

            if (finalQuery.isNotEmpty()) {
                val pairs = finalQuery.split("&")

                for (pair in pairs) {
                    val idx = pair.indexOf("=")
                    if (idx >= 0) {
                        val parameterKey = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                        val parameterValue = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")

                        if (parameterKey == key) {
                            return parameterValue
                        }
                    }
                }
            }

            return ""
        } catch (e: MalformedURLException) {
            ""
        }
    }

    /**
     * 判断某个URL是否有效。
     *
     * @param url 要解析的 URL 字符串
     */
    @JvmStatic
    fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 将Map参数转换为Query参数并以"&"进行拼接。如果提供了编码参数，那么键和值将会按照该编码进行URL编码。
     *
     * @param params 要转换的Map参数。Map中的每个条目都会被转换为"key=value"形式的字符串。
     * @param encoding 可选的字符集名称。如果提供了这个参数，那么键和值将会按照该编码进行URL编码。如果这个参数为null，那么键和值将不会被编码。
     * @return 转换并拼接后的字符串。如果提供了编码参数，那么这个字符串将会是URL编码的。
     */
    @JvmStatic
    @JvmOverloads
    fun splitParams(params: Map<String, String>, encoding: String? = null): String {
        return params.entries.joinToString("&") {
            val key = it.key
            val value = it.value
            if (encoding != null) {
                "${URLEncoder.encode(key, encoding)}=${URLEncoder.encode(value, encoding)}"
            } else {
                "$key=$value"
            }
        }
    }

    /**
     * 将"&"拼接的Query参数转换为Map。如果提供了解码参数，那么键和值将会按照该编码进行URL解码。
     *
     * @param query 要转换的"&"拼接的Query参数字符串。每个"key=value"形式的字符串都会被转换为Map中的一个条目。
     * @param encoding 可选的字符集名称。如果提供了这个参数，那么键和值将会按照该编码进行URL解码。如果这个参数为null，那么键和值将不会被解码。
     * @return 转换后的Map。如果提供了解码参数，那么这个Map的键和值将会是URL解码的。
     */
    @JvmStatic
    @JvmOverloads
    fun parseQuery(query: String, encoding: String? = null): Map<String, String> {
        return query.split("&")
            .filter { it.contains("=") }
            .associate {
                val parts = it.split("=")
                val key = parts[0]
                val value = parts[1]
                if (encoding != null) {
                    URLDecoder.decode(key, encoding) to URLDecoder.decode(value, encoding)
                } else {
                    key to value
                }
            }
    }
}
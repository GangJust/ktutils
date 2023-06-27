package com.freegang.ktutils.net

import android.annotation.SuppressLint
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

@SuppressLint("CustomX509TrustManager")
class TrustAllCerts : X509TrustManager {

    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        // 不做任何处理，接受所有客户端证书
    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        // 不做任何验证，接受所有服务器证书
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return emptyArray()
    }
}
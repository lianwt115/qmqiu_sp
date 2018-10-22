package com.lwt.qmqiu_sps1.utils

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Configuration
class OkHttpConfiguration {

    @Bean
    fun  okHttpClient(): OkHttpClient {
        return  OkHttpClient.Builder()
                //.sslSocketFactory(sslSocketFactory(), x509TrustManager())
                .retryOnConnectionFailure(false)
                .connectionPool(pool())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .build()
    }

   /* @Bean
    fun  x509TrustManager(): X509TrustManager {
        return  object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {

            }

            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {

            }

        }
    }*/

   /* @Bean
    fun  sslSocketFactory(): SSLSocketFactory? {
        try {
            //信任任何链接
            var sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null,  TrustManager[]{x509TrustManager()},  SecureRandom())
            return sslContext.socketFactory
        } catch ( e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch ( e: KeyManagementException) {
            e.printStackTrace()
        }
        return null
    }*/

    /**
     * Create a new connection pool with tuning parameters appropriate for a single-user application.
     * The tuning parameters in this pool are subject to change in future OkHttp releases. Currently
     */
    @Bean
    fun  pool(): ConnectionPool {
        return  ConnectionPool(200, 5, TimeUnit.MINUTES)
    }

}
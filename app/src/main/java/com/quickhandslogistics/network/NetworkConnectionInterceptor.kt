package com.quickhandslogistics.network

import android.content.Context
import android.net.ConnectivityManager
import com.quickhandslogistics.application.MyApplication
import io.bloco.faker.components.App
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkConnectionInterceptor : Interceptor {

    fun isNetwork(): Boolean {
        val connectivityManager = MyApplication.context().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager?.activeNetworkInfo
        val value = networkInfo != null && networkInfo.isConnected
        return value
    }
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetwork()) {
            throw NoConnectivityException()
        }
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
    inner class NoConnectivityException : IOException() {
        override fun getLocalizedMessage(): String {
            return "Network Connection exception"
        }
    }
}
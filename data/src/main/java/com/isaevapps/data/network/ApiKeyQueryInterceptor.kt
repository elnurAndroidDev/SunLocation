package com.isaevapps.data.network

import com.isaevapps.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyQueryInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val newUrl = original.url.newBuilder()
            .addQueryParameter("key", BuildConfig.API_KEY)
            .build()
        val newReq = original.newBuilder().url(newUrl).build()
        return chain.proceed(newReq)
    }
}
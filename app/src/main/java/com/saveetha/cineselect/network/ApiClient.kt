package com.saveetha.cineselect.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.util.Log

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    fun getInstance(context: Context): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                AuthTokenManager.getToken(context)?.let { token ->
                    Log.d("ApiClient", "Adding Authorization header with token: $token")
                    requestBuilder.header("Authorization", "Bearer $token")
                } ?: run {
                    Log.d("ApiClient", "No token found in AuthTokenManager.")
                }
                val request = requestBuilder.build()
                chain.proceed(request)
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
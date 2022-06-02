package ru.samsung.itacademy.mdev.getusdrate

import android.content.res.Resources
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

class NetworkClient {

    val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    fun request(url: String): String? {
        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("Apikey", "e8eb8873de9842290ec5be3dcd9e77761132bc79947e94f0934854fa7b8bd99e")
            .build()

        try {
            val response = client.newCall(requestBuilder).execute()
            if (response.isSuccessful) {
                return response.body?.string()
            }
        } catch (e: Exception) {
            Log.e("NetworkClient", "error during network call", e)
        }

        return null
    }
}
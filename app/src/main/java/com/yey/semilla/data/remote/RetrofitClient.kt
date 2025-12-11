package com.yey.semilla.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 🔹 Logger compartido para TODAS las APIs
    private val logging: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // ================== BACKEND SEMILLA (SPRING BOOT) ==================

    private val backendClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val backendRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)   // ej: "http://10.132.11.4:8080/"
            .client(backendClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: SemillaApi by lazy {
        backendRetrofit.create(SemillaApi::class.java)
    }

    // ================== OPEN-METEO (API EXTERNA) ==================

    private val openMeteoClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val openMeteoRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConstants.WEATHER_URL) // se llama al link a la API externa
            .client(openMeteoClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val openMeteoApi: OpenMeteoApi by lazy {
        openMeteoRetrofit.create(OpenMeteoApi::class.java)
    }
}

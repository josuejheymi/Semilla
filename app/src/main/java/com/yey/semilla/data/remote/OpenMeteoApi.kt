package com.yey.semilla.data.remote

import com.yey.semilla.data.remote.dto.OpenMeteoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {

    @GET("v1/air-quality")
    suspend fun getCurrentAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        // estos parámetros se envían igual que en la URL que probaste en el navegador
        @Query("current") current: String = "european_aqi,uv_index"
    ): OpenMeteoResponse
}

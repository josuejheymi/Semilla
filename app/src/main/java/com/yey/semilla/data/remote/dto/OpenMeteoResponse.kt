// OpenMeteoResponse.kt
package com.yey.semilla.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenMeteoResponse(
    val latitude: Double?,
    val longitude: Double?,
    val generationtime_ms: Double?,
    val utc_offset_seconds: Int?,
    val timezone: String?,
    val timezone_abbreviation: String?,
    val elevation: Double?,

    @SerializedName("current_units")
    val currentUnits: CurrentUnits?,

    @SerializedName("current")
    val current: Current?
)

data class CurrentUnits(
    val time: String?,

    @SerializedName("european_aqi")
    val europeanAqi: String?,

    @SerializedName("uv_index")
    val uvIndex: String?
)

data class Current(
    val time: String?,

    // OJO: aquí es donde importa el nombre exacto
    @SerializedName("european_aqi")
    val europeanAqi: Double?,

    @SerializedName("uv_index")
    val uvIndex: Double?
)

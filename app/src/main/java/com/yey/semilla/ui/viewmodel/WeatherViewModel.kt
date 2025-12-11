package com.yey.semilla.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yey.semilla.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado que usará la UI (HomeScreen)
data class AirQualityUiState(
    val uvIndex: Double? = null,
    val aqi: Double? = null,         // usamos AQI europeo en lugar de pm2.5
    val error: String? = null,
    val isLoading: Boolean = false
)

class WeatherViewModel : ViewModel() {

    private val _state = MutableStateFlow(AirQualityUiState())
    val state: StateFlow<AirQualityUiState> = _state.asStateFlow()

    fun loadAirQuality(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                // Marcamos que está cargando
                _state.value = _state.value.copy(
                    isLoading = true,
                    error = null
                )

                // Llamada a la API (tu endpoint actual)
                val response = RetrofitClient.openMeteoApi.getCurrentAirQuality(
                    latitude = latitude,
                    longitude = longitude
                )

                val current = response.current
                val uv = current?.uvIndex          // mapeado desde "uv_index"
                val aqi = current?.europeanAqi     // mapeado desde "european_aqi"

                Log.d("OpenMeteo", "current=$current, uv=$uv, aqi=$aqi")

                _state.value = AirQualityUiState(
                    uvIndex = uv,
                    aqi = aqi,
                    isLoading = false,
                    error = null
                )

            } catch (e: Exception) {
                Log.e("OpenMeteo", "Error cargando calidad de aire", e)
                _state.value = AirQualityUiState(
                    uvIndex = null,
                    aqi = null,
                    isLoading = false,
                    error = e.message ?: "Error al cargar datos"
                )
            }
        }
    }
}

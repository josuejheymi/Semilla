package com.yey.semilla.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yey.semilla.domain.model.MedicationEntity
import com.yey.semilla.domain.repository.MedicationRepository
import kotlinx.coroutines.Job // Importante importar Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicationViewModel(
    private val repository: MedicationRepository
) : ViewModel() {

    private val _medications = MutableStateFlow<List<MedicationEntity>>(emptyList())
    val medications: StateFlow<List<MedicationEntity>> = _medications.asStateFlow()

    // Variable para controlar la "suscripción" a la base de datos
    private var loadJob: Job? = null

    fun loadMedications(userId: Int) {
        // 1. Si ya había una escucha activa, la cancelamos para no tener dobles
        loadJob?.cancel()

        // 2. Iniciamos la nueva escucha y guardamos la referencia en loadJob
        loadJob = viewModelScope.launch {
            repository.getMedicationsByUser(userId).collect { list ->
                // Aquí REEMPLAZAMOS la lista, así que si hay duplicados,
                // es culpa de la base de datos, no del ViewModel.
                _medications.value = list
            }
        }
    }

    fun addMedication(med: MedicationEntity) {
        viewModelScope.launch {
            repository.addMedication(med)
        }
    }
}
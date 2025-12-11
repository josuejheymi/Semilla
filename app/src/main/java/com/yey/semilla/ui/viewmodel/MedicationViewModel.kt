package com.yey.semilla.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yey.semilla.domain.model.MedicationEntity
import com.yey.semilla.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 💡 VIEW MODEL DE MEDICAMENTOS (Capa de Presentación)
 *
 * Gestiona el estado de los medicamentos y se comunica con el repositorio.
 */
class MedicationViewModel(
    private val repository: MedicationRepository
) : ViewModel() {

    // Estado observable con la lista de medicamentos del usuario
    private val _medications = MutableStateFlow<List<MedicationEntity>>(emptyList())
    val medications: StateFlow<List<MedicationEntity>> = _medications.asStateFlow()

    /**
     * Cargar medicamentos del usuario desde el repositorio (Room + posible backend).
     */
    fun loadMedications(userId: Int) {
        viewModelScope.launch {
            repository.getMedicationsByUser(userId).collect { list ->
                _medications.value = list
            }
        }
    }

    /**
     * Agregar un nuevo medicamento (se guarda vía repositorio).
     */
    fun addMedication(med: MedicationEntity) {
        viewModelScope.launch {
            repository.addMedication(med)
        }
    }
}

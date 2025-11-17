package com.yey.semilla.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yey.semilla.data.local.model.MedicationEntity
import com.yey.semilla.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
/**
 * 💡 VIEW MODEL DE MEDICAMENTOS (Capa de Presentación)
 *
 * Propósito: Clase responsable de gestionar los datos relacionados con los medicamentos
 * y preparar el estado para ser consumido por la Interfaz de Usuario (UI).
 *
 * 1. Estado Reactivo: Utiliza 'MutableStateFlow' (_medications) para contener la lista
 * de medicamentos. Este es el dato que la UI observa para redibujarse automáticamente.
 *
 * 2. Acceso al Repositorio: Inyecta el 'MedicationRepository' para acceder a la base de datos.
 *
 * 3. Corrutinas: Utiliza 'viewModelScope.launch' para ejecutar operaciones asíncronas
 * (como las del repositorio) de forma segura y atada al ciclo de vida del ViewModel.
 */
class MedicationViewModel(private val repository: MedicationRepository) : ViewModel() {

    private val _medications = MutableStateFlow<List<MedicationEntity>>(emptyList())
    val medications: StateFlow<List<MedicationEntity>> = _medications.asStateFlow()

    fun loadMedications(userId: Int) {
        viewModelScope.launch {
            repository.getMedicationsByUser(userId).collect { list ->
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
package com.yey.semilla.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yey.semilla.data.local.model.ReminderEntity
import com.yey.semilla.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
/**
 * 💡 VIEW MODEL DE RECORDATORIOS (Capa de Presentación)
 *
 * Propósito: Gestionar y preparar los datos de los recordatorios para la Interfaz de Usuario (UI).
 * Es el puente entre el Repositorio de Recordatorios y la vista.
 *
 * 1. Estado: Utiliza 'StateFlow' para exponer la lista de recordatorios. Esto permite que
 * la UI observe los cambios en tiempo real.
 *
 * 2. Carga: La función 'loadReminders' usa el 'medicationId' para obtener los recordatorios
 * específicos de ese medicamento y los recolecta continuamente (collect) desde el Flow del repositorio.
 *
 * 3. Operaciones: La función 'addReminder' delega la inserción asíncrona al repositorio
 * dentro de un 'viewModelScope' para gestión del ciclo de vida.
 */
class ReminderViewModel(private val repository: ReminderRepository) : ViewModel() {

    private val _reminders = MutableStateFlow<List<ReminderEntity>>(emptyList())
    val reminders: StateFlow<List<ReminderEntity>> = _reminders.asStateFlow()

    fun loadReminders(medicationId: Int) {
        viewModelScope.launch {
            repository.getRemindersByMedication(medicationId).collect { list ->
                _reminders.value = list
            }
        }
    }

    fun addReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.addReminder(reminder)
        }
    }
}
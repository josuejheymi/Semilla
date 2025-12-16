package com.yey.semilla.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yey.semilla.domain.model.MedicationEntity
import com.yey.semilla.domain.model.ReminderEntity
import com.yey.semilla.domain.model.ReminderWithMedication
import com.yey.semilla.domain.repository.ReminderRepository
import kotlinx.coroutines.Job // Importante
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _reminders = MutableStateFlow<List<ReminderWithMedication>>(emptyList())
    val reminders: StateFlow<List<ReminderWithMedication>> = _reminders.asStateFlow()

    private val _medications = MutableStateFlow<List<MedicationEntity>>(emptyList())
    val medications: StateFlow<List<MedicationEntity>> = _medications.asStateFlow()

    private var currentUserId: Int? = null

    // Variables para controlar las tareas de carga y poder cancelarlas
    private var remindersJob: Job? = null
    private var medicationsJob: Job? = null

    // ---------------------------------------------------------
    // 🔥 Cargar datos del usuario actual (CORREGIDO)
    // ---------------------------------------------------------
    fun loadForUser(userId: Int) {
        // Si ya estamos cargando datos de ESTE usuario, no hacemos nada para evitar parpadeos
        if (currentUserId == userId && _reminders.value.isNotEmpty()) return

        currentUserId = userId

        // 1. Cancelamos trabajos anteriores si existían
        remindersJob?.cancel()
        medicationsJob?.cancel()

        // 2. Iniciamos nueva escucha de Recordatorios
        remindersJob = viewModelScope.launch {
            repository.getUserReminders(userId).collect { list ->
                _reminders.value = list
            }
        }

        // 3. Iniciamos nueva escucha de Medicamentos
        medicationsJob = viewModelScope.launch {
            repository.getUserMedications(userId).collect { list ->
                _medications.value = list
            }
        }
    }

    // ... el resto de tus funciones (addReminder, updateReminder, etc.) siguen igual ...
    fun addReminder(
        medicationId: Int,
        startDate: Long,
        endDate: Long? = null,
        timesPerDay: Int = 1,
        time: String,
        isEnabled: Boolean = true
    ) = viewModelScope.launch {
        val userId = currentUserId ?: return@launch
        val reminder = ReminderEntity(
            medicationId = medicationId,
            userId = userId,
            startDate = startDate,
            endDate = endDate,
            timesPerDay = timesPerDay,
            time = time,
            isEnabled = isEnabled
        )
        repository.addReminder(reminder)
    }

    // ... agrega aquí tus otras funciones addMedication, update, delete ...
    fun addMedication(med: MedicationEntity) = viewModelScope.launch {
        repository.addMedication(med)
    }

    fun updateReminder(reminder: ReminderEntity) = viewModelScope.launch {
        repository.updateReminder(reminder)
    }

    fun deleteReminder(reminder: ReminderEntity) = viewModelScope.launch {
        repository.deleteReminder(reminder)
    }

    fun toggleReminderEnabled(reminder: ReminderEntity) = viewModelScope.launch {
        val updated = reminder.copy(isEnabled = !reminder.isEnabled)
        repository.updateReminder(updated)
    }
}
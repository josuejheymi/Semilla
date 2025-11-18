package com.yey.semilla.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yey.semilla.data.local.model.MedicationEntity
import com.yey.semilla.data.local.model.ReminderEntity
import com.yey.semilla.data.local.model.ReminderWithMedication
import com.yey.semilla.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val repository: ReminderRepository,
    private val userId: Int
) : ViewModel() {

    // Lista de recordatorios
    private val _reminders = MutableStateFlow<List<ReminderWithMedication>>(emptyList())
    val reminders: StateFlow<List<ReminderWithMedication>> = _reminders.asStateFlow()

    // Lista de medicamentos
    private val _medications = MutableStateFlow<List<MedicationEntity>>(emptyList())
    val medications: StateFlow<List<MedicationEntity>> = _medications.asStateFlow()

    init {
        // Cargar recordatorios
        viewModelScope.launch {
            repository.getUserReminders(userId).collect { _reminders.value = it }
        }

        // Cargar medicamentos
        viewModelScope.launch {
            repository.getUserMedications(userId).collect { _medications.value = it }
        }
    }

    // -------------------- NUEVO: Crear recordatorio completo --------------------
    fun addReminder(
        medicationId: Int,
        startDate: Long,
        endDate: Long? = null,
        timesPerDay: Int = 1,
        time: String,
        isEnabled: Boolean = true
    ) = viewModelScope.launch {
        val reminder = ReminderEntity(
            medicationId = medicationId,
            startDate = startDate,
            endDate = endDate,
            timesPerDay = timesPerDay,
            time = time,
            isEnabled = isEnabled
        )
        repository.addReminder(reminder)
    }
    fun addMedication(med: MedicationEntity) = viewModelScope.launch {
        repository.addMedication(med)
    }


    fun updateReminder(reminder: ReminderEntity) = viewModelScope.launch {
        repository.updateReminder(reminder)
    }

    fun deleteReminder(reminder: ReminderEntity) = viewModelScope.launch {
        repository.deleteReminder(reminder)
    }

    // -------------------- NUEVO: Activar/Desactivar recordatorio --------------------
    fun toggleReminderEnabled(reminder: ReminderEntity) = viewModelScope.launch {
        val updated = reminder.copy(isEnabled = !reminder.isEnabled)
        repository.updateReminder(updated)
    }
}

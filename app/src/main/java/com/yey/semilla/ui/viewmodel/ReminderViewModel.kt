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

    fun addReminder(reminder: ReminderEntity) = viewModelScope.launch {
        repository.addReminder(reminder)
    }

    fun updateReminder(reminder: ReminderEntity) = viewModelScope.launch {
        repository.updateReminder(reminder)
    }

    fun deleteReminder(reminder: ReminderEntity) = viewModelScope.launch {
        repository.deleteReminder(reminder)
    }
}

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
    private val repository: ReminderRepository
) : ViewModel() {

    private val _reminders = MutableStateFlow<List<ReminderWithMedication>>(emptyList())
    val reminders: StateFlow<List<ReminderWithMedication>> = _reminders.asStateFlow()

    private val _medications = MutableStateFlow<List<MedicationEntity>>(emptyList())
    val medications: StateFlow<List<MedicationEntity>> = _medications.asStateFlow()

    // 👉 usuario actual dinámico
    private var currentUserId: Int? = null

    /**
     * 🔥 Cargar datos del usuario actual
     */
    fun loadForUser(userId: Int) {
        currentUserId = userId

        viewModelScope.launch {
            repository.getUserReminders(userId).collect { list ->
                _reminders.value = list
            }
        }

        viewModelScope.launch {
            repository.getUserMedications(userId).collect { list ->
                _medications.value = list
            }
        }
    }

    /**
     * 🔥 Crear recordatorio ATADO al usuario correcto
     */
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
            userId = userId,   // ahora SIEMPRE el usuario correcto
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

    fun toggleReminderEnabled(reminder: ReminderEntity) = viewModelScope.launch {
        val updated = reminder.copy(isEnabled = !reminder.isEnabled)
        repository.updateReminder(updated)
    }
}

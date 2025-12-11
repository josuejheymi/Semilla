package com.yey.semilla.domain.repository

import com.yey.semilla.domain.model.MedicationEntity
import com.yey.semilla.domain.model.ReminderEntity
import com.yey.semilla.domain.model.ReminderWithMedication
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    fun getUserReminders(userId: Int): Flow<List<ReminderWithMedication>>

    fun getUserMedications(userId: Int): Flow<List<MedicationEntity>>

    suspend fun addReminder(reminder: ReminderEntity)


    suspend fun updateReminder(reminder: ReminderEntity)

    suspend fun deleteReminder(reminder: ReminderEntity)

    // 🔥 ESTA ES LA FUNCIÓN QUE TE FALTABA
    suspend fun addMedication(med: MedicationEntity)
}

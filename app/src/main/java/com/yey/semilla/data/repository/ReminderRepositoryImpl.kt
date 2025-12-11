// ReminderRepositoryImpl.kt
package com.yey.semilla.data.repository

import android.util.Log
import com.yey.semilla.data.local.dao.MedicationDao
import com.yey.semilla.data.local.dao.ReminderDao
import com.yey.semilla.data.remote.SemillaApi
import com.yey.semilla.data.remote.dto.ReminderNetworkDto
import com.yey.semilla.domain.model.MedicationEntity
import com.yey.semilla.domain.model.ReminderEntity
import com.yey.semilla.domain.model.ReminderWithMedication
import com.yey.semilla.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
    private val medicationDao: MedicationDao,
    private val api: SemillaApi          // 👈 ya lo pasaste en MainActivity
) : ReminderRepository {

    // ---------- RECORDATORIOS ----------

    override fun getUserReminders(userId: Int): Flow<List<ReminderWithMedication>> {
        return reminderDao.getRemindersWithMedicationByUser(userId)
            .onStart {
                try {
                    // 1) Traemos lista desde backend
                    val remote = api.getRemindersByUser(userId)

                    if (remote.isNotEmpty()) {
                        // 2) Mapear DTO → entidades Room
                        val remindersRoom = remote.map { dto ->
                            ReminderEntity(
                                id = dto.id?.toInt() ?: 0,
                                userId = dto.userId,
                                medicationId = dto.medicationId,
                                startDate = dto.startDate,
                                endDate = dto.endDate,
                                timesPerDay = dto.timesPerDay,
                                time = dto.time,
                                isEnabled = dto.isEnabled
                            )
                        }

                        // 3) Podrías hacer un replaceAll si tienes ese método, o limpiar antes
                        reminderDao.insertAll(remindersRoom)
                        Log.d("ReminderRepo", "🔄 Sincronizados ${remindersRoom.size} recordatorios desde backend")
                    }
                } catch (e: Exception) {
                    Log.e("ReminderRepo", "⚠️ No se pudo sincronizar recordatorios: ${e.message}")
                }
            }
    }

    override fun getUserMedications(userId: Int): Flow<List<MedicationEntity>> {
        return medicationDao.getMedicationsByUser(userId)
    }

    override suspend fun addReminder(reminder: ReminderEntity) {
        // 1) Guardar en Room primero
        reminderDao.addReminder(reminder)

        // 2) Intentar mandar al backend
        try {
            val dto = ReminderNetworkDto(
                id = null,
                userId = reminder.userId,
                medicationId = reminder.medicationId,
                startDate = reminder.startDate,
                endDate = reminder.endDate,
                timesPerDay = reminder.timesPerDay,
                time = reminder.time,
                isEnabled = reminder.isEnabled
            )

            val created = api.createReminderForUser(reminder.userId, dto)
            Log.d("ReminderRepo", "✅ Recordatorio creado en backend con id ${created.id}")
        } catch (e: Exception) {
            Log.e("ReminderRepo", "⚠️ No se pudo enviar el recordatorio al backend: ${e.message}")
        }
    }

    override suspend fun updateReminder(reminder: ReminderEntity) {
        // De momento solo local. Luego podrías agregar PUT al backend.
        reminderDao.updateReminder(reminder)
    }

    override suspend fun deleteReminder(reminder: ReminderEntity) {
        // De momento solo local. Luego podrías agregar DELETE al backend.
        reminderDao.deleteReminder(reminder)
    }

    override suspend fun addMedication(med: MedicationEntity) {
        medicationDao.addMedication(med)
    }
}

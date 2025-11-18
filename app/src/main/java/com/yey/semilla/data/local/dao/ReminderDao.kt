package com.yey.semilla.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.yey.semilla.data.local.model.ReminderEntity
import com.yey.semilla.data.local.model.ReminderWithMedication
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    // Traer todos los recordatorios con su medicamento
    @Transaction
    @Query("""
        SELECT * FROM reminders
        INNER JOIN medications ON reminders.medicationId = medications.id
        WHERE medications.userId = :userId
    """)
    fun getRemindersWithMedicationByUser(userId: Int): Flow<List<ReminderWithMedication>>

    @Insert
    suspend fun addReminder(reminder: ReminderEntity)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)
}
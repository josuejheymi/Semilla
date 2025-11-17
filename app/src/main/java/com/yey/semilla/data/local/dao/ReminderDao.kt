package com.yey.semilla.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yey.semilla.data.local.model.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert
    suspend fun insert(rem: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE medicationId = :medicationId")
    fun getByMedication(medicationId: Int): Flow<List<ReminderEntity>>
}
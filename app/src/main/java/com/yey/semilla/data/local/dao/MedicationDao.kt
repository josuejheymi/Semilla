package com.yey.semilla.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yey.semilla.domain.model.MedicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMedication(medication: MedicationEntity)

    // 🔹 Obtener medicamentos de un usuario
    @Query("SELECT * FROM medications WHERE userId = :userId")
    fun getMedicationsByUser(userId: Int): Flow<List<MedicationEntity>>

    // 🔹 Insertar muchos (para sincronizar con el backend)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medications: List<MedicationEntity>)
}

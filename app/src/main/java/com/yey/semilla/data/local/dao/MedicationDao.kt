package com.yey.semilla.data.local.dao

import androidx.room.*
import com.yey.semilla.data.local.model.MedicationEntity
import kotlinx.coroutines.flow.Flow
// DAO: Define los métodos para realizar operaciones CRUD (Crear, Leer, Actualizar, Borrar) en la tabla 'medications'. Es la interfaz de comunicación con Room.
@Dao
interface MedicationDao {
    @Insert
    suspend fun insert(med: MedicationEntity)

    @Query("SELECT * FROM medications WHERE userId = :userId")
    // 'Flow': Devuelve un flujo de datos que se actualiza automáticamente. Cada vez que cambian
    // los medicamentos en la DB, el Flow emite la nueva lista al ViewModel.
    fun getByUser(userId: Int): Flow<List<MedicationEntity>>
}
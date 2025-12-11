package com.yey.semilla.domain.repository

import com.yey.semilla.domain.model.MedicationEntity
import kotlinx.coroutines.flow.Flow

interface MedicationRepository {

    // Obtener medicamentos por usuario (Flow para la UI reactiva)
    fun getMedicationsByUser(userId: Int): Flow<List<MedicationEntity>>

    // Agregar medicamento (local + backend)
    suspend fun addMedication(medication: MedicationEntity)
}

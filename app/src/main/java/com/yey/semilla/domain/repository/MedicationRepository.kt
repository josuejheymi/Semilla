package com.yey.semilla.domain.repository

import com.yey.semilla.data.local.model.MedicationEntity
import kotlinx.coroutines.flow.Flow
/**
 * 💊 CONTRATO DEL REPOSITORIO DE MEDICAMENTOS (Capa de Dominio)
 * * Propósito: Define el conjunto de operaciones esenciales para gestionar los datos de los
 * medicamentos. Aísla la lógica de negocio (ViewModel) de cómo se accede realmente a los datos (Room/API).
 * * Métodos Mínimos:
 * - addMedication: Inserta un nuevo medicamento de forma asíncrona.
 * - getMedicationsByUser: Devuelve un Flow continuo con la lista de medicamentos
 * para un usuario específico (filtrado).
 * * Sugerencias: Aquí se pueden añadir métodos para actualizar dosis, eliminar medicamentos, o
 * descontar pastillas (ej: 'updatePillsRemaining(medId: Int, newCount: Int)').
 */
interface MedicationRepository {
    // Inserta un nuevo medicamento de forma asíncrona.
    suspend fun addMedication(med: MedicationEntity)

    // Obtiene un flujo de datos continuo y filtrado por usuario.
    fun getMedicationsByUser(userId: Int): Flow<List<MedicationEntity>>
}
package com.yey.semilla.domain.repository

import com.yey.semilla.data.local.model.ReminderEntity
import kotlinx.coroutines.flow.Flow
/**
 * ⏰ CONTRATO DEL REPOSITORIO DE RECORDATORIOS (Capa de Dominio)
 * * Propósito: Define las operaciones esenciales para gestionar los recordatorios asociados a la toma
 * de medicamentos. Asegura que la lógica de negocio (ViewModel) no dependa directamente de la fuente
 * de datos (Room/API).
 * * Métodos Mínimos:
 * - addReminder: Crea un nuevo registro de recordatorio de forma asíncrona.
 * - getRemindersByMedication: Devuelve un Flow continuo con los recordatorios
 * filtrados por un medicamento específico (usando su 'medicationId').
 * * Sugerencias: Aquí se deberían añadir métodos para actualizar o eliminar recordatorios
 * específicos (ej: 'deleteReminder(reminderId: Int)').
 */
interface ReminderRepository {
    // Inserta un nuevo recordatorio de forma asíncrona.
    suspend fun addReminder(reminder: ReminderEntity)

    // Obtiene un flujo de datos continuo de recordatorios filtrados por ID de medicamento.
    fun getRemindersByMedication(medicationId: Int): Flow<List<ReminderEntity>>
}
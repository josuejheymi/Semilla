package com.yey.semilla.domain.repository

import com.yey.semilla.data.local.model.MedicationEntity
import com.yey.semilla.data.local.model.ReminderEntity
import com.yey.semilla.data.local.model.ReminderWithMedication
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

    // Recordatorios del usuario
    fun getUserReminders(userId: Int): Flow<List<ReminderWithMedication>>

    // Medicamentos del usuario
    fun getUserMedications(userId: Int): Flow<List<MedicationEntity >>

    // Operaciones CRUD de recordatorios
    suspend fun addReminder(reminder: ReminderEntity)
    suspend fun updateReminder(reminder: ReminderEntity)
    suspend fun deleteReminder(reminder: ReminderEntity)
}


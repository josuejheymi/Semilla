package com.yey.semilla.domain.repository

import com.yey.semilla.data.local.dao.ReminderDao
import com.yey.semilla.data.local.model.ReminderEntity
import kotlinx.coroutines.flow.Flow
/**
 * ⏰ IMPLEMENTACIÓN DEL REPOSITORIO DE RECORDATORIOS (Capa de Datos)
 * * Propósito: Clase concreta que implementa el contrato 'ReminderRepository'.
 * Su función es conectar la lógica de negocio (Dominio) con las operaciones reales de la DB (Room).
 * * Contenido: Recibe el 'ReminderDao' y traduce los métodos abstractos del repositorio
 * (ej: 'addReminder') a las llamadas directas de Room (ej: 'reminderDao.insert()').
 * * Flujo de Datos: Garantiza que la lectura de recordatorios se haga a través de un Flow,
 * lo que permite que el ViewModel reaccione automáticamente a cualquier cambio en la base de datos.
 */
class ReminderRepositoryImpl(private val reminderDao: ReminderDao) : ReminderRepository {

    override suspend fun addReminder(reminder: ReminderEntity) {
        // Llama a la función de inserción del DAO de Room.
        reminderDao.insert(reminder)
    }

    override fun getRemindersByMedication(medicationId: Int): Flow<List<ReminderEntity>> {
        // Devuelve el Flow filtrado por ID de medicamento desde el DAO.
        return reminderDao.getByMedication(medicationId)
    }
}
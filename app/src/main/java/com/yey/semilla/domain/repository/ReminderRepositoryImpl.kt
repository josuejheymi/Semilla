/**
 * ⏰ IMPLEMENTACIÓN DEL REPOSITORIO DE RECORDATORIOS (Capa de Datos)
 * * Propósito: Clase concreta que implementa el contrato 'ReminderRepository'.
 * Su función es conectar la lógica de negocio (Dominio) con las operaciones reales de la DB (Room).
 * * Contenido: Recibe el 'ReminderDao' y traduce los métodos abstractos del repositorio
 * (ej: 'addReminder') a las llamadas directas de Room (ej: 'reminderDao.insert()').
 * * Flujo de Datos: Garantiza que la lectura de recordatorios se haga a través de un Flow,
 * lo que permite que el ViewModel reaccione automáticamente a cualquier cambio en la base de datos.
 */
package com.yey.semilla.domain.repository

import com.yey.semilla.data.local.dao.MedicationDao
import com.yey.semilla.data.local.dao.ReminderDao
import com.yey.semilla.data.local.model.MedicationEntity
import com.yey.semilla.data.local.model.ReminderEntity
import com.yey.semilla.data.local.model.ReminderWithMedication
import com.yey.semilla.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
    private val medicationDao: MedicationDao
) : ReminderRepository {

    override fun getUserReminders(userId: Int): Flow<List<ReminderWithMedication>> {
        return reminderDao.getRemindersWithMedicationByUser(userId)
    }

    override fun getUserMedications(userId: Int): Flow<List<MedicationEntity>> {
        return medicationDao.getByUser(userId)
    }

    override suspend fun addReminder(reminder: ReminderEntity) {
        reminderDao.addReminder(reminder)
    }

    override suspend fun updateReminder(reminder: ReminderEntity) {
        reminderDao.updateReminder(reminder)
    }

    override suspend fun deleteReminder(reminder: ReminderEntity) {
        reminderDao.deleteReminder(reminder)
    }

    // 🔥 MÉTODO FALTANTE (AGREGA ESTO)
    override suspend fun addMedication(med: MedicationEntity) {
        medicationDao.addMedication(med)
    }
}

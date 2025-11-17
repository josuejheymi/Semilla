package com.yey.semilla.domain.repository

import com.yey.semilla.data.local.dao.MedicationDao
import com.yey.semilla.data.local.model.MedicationEntity
import kotlinx.coroutines.flow.Flow
/**
 * 💊 IMPLEMENTACIÓN DEL REPOSITORIO DE MEDICAMENTOS (Capa de Datos)
 * * Propósito: Clase responsable de implementar el contrato 'MedicationRepository'.
 * Actúa como un puente entre la capa de Dominio (lógica de negocio) y la fuente de datos local (Room).
 * * Contenido: Traduce las peticiones abstractas del dominio (ej: 'addMedication')
 * a las llamadas concretas del DAO de Room (ej: 'medicationDao.insert()').
 * * Dependencias: Inyecta el 'MedicationDao' para acceder a las operaciones de la DB.
 * * Extensión: Si se usaran APIs externas, aquí se definiría la lógica para obtener,
 * guardar o sincronizar medicamentos entre el servidor y Room.
 */
class MedicationRepositoryImpl(private val medicationDao: MedicationDao) : MedicationRepository {

    override suspend fun addMedication(med: MedicationEntity) {
        // Llama a la función de inserción del DAO de Room.
        medicationDao.insert(med)
    }

    override fun getMedicationsByUser(userId: Int): Flow<List<MedicationEntity>> {
        // Devuelve el Flow de Room, asegurando actualizaciones en tiempo real
        return medicationDao.getByUser(userId)
    }
}
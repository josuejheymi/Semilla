package com.yey.semilla.data.repository

import android.util.Log
import com.yey.semilla.data.local.dao.MedicationDao
import com.yey.semilla.data.remote.SemillaApi
import com.yey.semilla.data.remote.dto.MedicationNetworkDto
import com.yey.semilla.domain.model.MedicationEntity
import com.yey.semilla.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

/**
 * 💊 IMPLEMENTACIÓN DEL REPOSITORIO DE MEDICAMENTOS
 * - Lee SIEMPRE desde Room (Flow)
 * - Intenta sincronizar con el backend cuando se pueda
 */
class MedicationRepositoryImpl(
    private val medicationDao: MedicationDao,
    private val api: SemillaApi
) : MedicationRepository {

    override fun getMedicationsByUser(userId: Int): Flow<List<MedicationEntity>> {
        return medicationDao.getMedicationsByUser(userId)
            .onStart {
                try {
                    // 1) Pedimos al backend
                    val remoteList = api.getMedicationsByUser(userId.toLong())

                    // 2) Mapeamos DTO -> Entity (para Room)
                    val entities = remoteList.map { dto ->
                        MedicationEntity(
                            id = dto.id?.toInt() ?: 0,
                            userId = dto.userId?.toInt() ?: userId,
                            name = dto.name,
                            totalPills = dto.totalPills,
                            pillsRemaining = dto.pillsRemaining,
                            imageUri = dto.imageUri
                        )
                    }

                    // 3) Guardamos/actualizamos en Room
                    if (entities.isNotEmpty()) {
                        medicationDao.insertAll(entities)
                        Log.d(
                            "MedicationRepo",
                            "🔄 Sincronizados ${entities.size} medicamentos desde backend"
                        )
                    }
                } catch (e: Exception) {
                    Log.e(
                        "MedicationRepo",
                        "⚠️ No se pudo sincronizar medicamentos: ${e.message}"
                    )
                }
            }
    }
    override suspend fun addMedication(medication: MedicationEntity) {
        // 1. Guardamos localmente para que se vea rápido (efecto instantáneo)
        // CAPTURAMOS el ID temporal que nos da Room (ej: ID 1)
        val localId = medicationDao.addMedication(medication).toInt()

        try {
            // 2. Preparamos el objeto para enviar (id null para que el backend cree uno nuevo)
            val dto = MedicationNetworkDto(
                id = null,
                userId = medication.userId.toLong(),
                name = medication.name,
                totalPills = medication.totalPills,
                pillsRemaining = medication.pillsRemaining,
                imageUri = medication.imageUri
            )

            // 3. Enviamos a internet
            // El backend nos devuelve el objeto FINAL con el ID real (ej: ID 500)
            val createdDto = api.createMedicationForUser(
                userId = medication.userId.toLong(),
                medication = dto
            )

            Log.d("MedicationRepo", "✅ Creado en nube con ID: ${createdDto.id}")

            // ================================================================
            // 🛑 AQUÍ ESTÁ LA MAGIA PARA EVITAR DUPLICADOS
            // ================================================================

            // 4. Borramos el "borrador" local (el ID 1) porque ya no lo necesitamos
            medicationDao.deleteMedicationById(localId)

            // 5. Insertamos el oficial que vino de internet (el ID 500)
            val oficialEntity = MedicationEntity(
                id = createdDto.id?.toInt() ?: 0,
                userId = createdDto.userId?.toInt() ?: medication.userId,
                name = createdDto.name,
                totalPills = createdDto.totalPills,
                pillsRemaining = createdDto.pillsRemaining,
                imageUri = createdDto.imageUri
            )
            medicationDao.addMedication(oficialEntity)

        } catch (e: Exception) {
            Log.e("MedicationRepo", "⚠️ Error subiendo: ${e.message}")
            // Si falla internet, NO borramos el localId.
            // Se queda el ID 1 para que el usuario no pierda su dato.
        }
    }
}

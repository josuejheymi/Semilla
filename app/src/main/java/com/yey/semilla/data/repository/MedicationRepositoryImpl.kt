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
        // 1) Guardar local (para que la UI lo vea al toque)
        medicationDao.addMedication(medication)

        // 2) Intentar mandarlo al backend
        try {
            val dto = MedicationNetworkDto(
                id = null,  // 💥 IMPORTANTE: null para que el backend genere el ID
                userId = medication.userId.toLong(),
                name = medication.name,
                totalPills = medication.totalPills,
                pillsRemaining = medication.pillsRemaining,
                imageUri = medication.imageUri
            )

            val created = api.createMedicationForUser(
                userId = medication.userId.toLong(),
                medication = dto
            )

            Log.d(
                "MedicationRepo",
                "✅ Medicamento creado en backend con id ${created.id}"
            )
        } catch (e: Exception) {
            Log.e(
                "MedicationRepo",
                "⚠️ No se pudo enviar el medicamento al backend: ${e.message}"
            )
            // Si quieres, aquí podrías marcarlo como "pendiente de sincronizar"
        }
    }
}

package com.yey.semilla.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

// Relación entre Reminder y Medication
data class ReminderWithMedication(
    @Embedded val reminder: ReminderEntity,
    @Relation(
        parentColumn = "medicationId",
        entityColumn = "id"
    )
    val medication: MedicationEntity
)

// Relación entre User y sus Medicamentos
data class UserWithMedications(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val medications: List<MedicationEntity>
)

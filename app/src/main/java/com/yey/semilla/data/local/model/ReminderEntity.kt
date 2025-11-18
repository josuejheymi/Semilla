package com.yey.semilla.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicationId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicationId: Int,
    val startDate: Long,          // timestamp de inicio
    val endDate: Long? = null,    // opcional, fecha de fin
    val timesPerDay: Int = 1,     // cuántas veces se tomará al día
    val time: String,             // hora (ej: "08:00")
    val isEnabled: Boolean = true
)
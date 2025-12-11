package com.yey.semilla.data.remote.dto

/**
 * DTO para la API de Spring (calza con MedicationDto del backend)
 */
data class MedicationNetworkDto(
    val id: Long? = null,        // null al crear
    val userId: Long? = null,    // opcional en el body (se usa el path)
    val name: String,
    val totalPills: Int,
    val pillsRemaining: Int,
    val imageUri: String? = null
)

// SemillaApi.kt
package com.yey.semilla.data.remote

import com.yey.semilla.data.remote.dto.MedicationNetworkDto
import com.yey.semilla.data.remote.dto.ReminderNetworkDto
import com.yey.semilla.domain.model.MedicationEntity
import com.yey.semilla.domain.model.UserEntity
import retrofit2.http.*

interface SemillaApi {

    // =============== USERS ===============
    @GET("api/users")
    suspend fun getUsers(): List<UserEntity>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserEntity

    @POST("api/users")
    suspend fun createUser(@Body user: com.yey.semilla.data.remote.dto.UserNetworkDto)
            : com.yey.semilla.data.remote.dto.UserNetworkDto

    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body user: com.yey.semilla.data.remote.dto.UserNetworkDto
    ): com.yey.semilla.data.remote.dto.UserNetworkDto

    // =============== MEDICATIONS ===============
    @GET("api/users/{userId}/medications")
    suspend fun getMedicationsByUser(
        @Path("userId") userId: Long
    ): List<MedicationEntity>

    // POST /api/users/{userId}/medications -> crea medicamento para un usuario
    @POST("api/users/{userId}/medications")
    suspend fun createMedicationForUser(
        @Path("userId") userId: Long,
        @Body medication: MedicationNetworkDto
    ): MedicationNetworkDto

    // =============== REMINDERS ===============

    // Traer todos los recordatorios de un usuario
    @GET("api/users/{userId}/reminders")
    suspend fun getRemindersByUser(
        @Path("userId") userId: Int
    ): List<ReminderNetworkDto>

    // Crear recordatorio para un usuario
    @POST("api/users/{userId}/reminders")
    suspend fun createReminderForUser(
        @Path("userId") userId: Int,
        @Body reminder: ReminderNetworkDto
    ): ReminderNetworkDto
}

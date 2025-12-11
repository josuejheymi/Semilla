package com.yey.semilla.data.remote.dto

data class UserNetworkDto(
    val id: Long? = null,          // null para nuevos usuarios
    val name: String,
    val email: String,
    val password: String,
    val photoUri: String? = null,
    val fechanacimiento: Long,     // mismo nombre que en el backend
    val genero: String,
    val peso: Double,
    val altura: Double,
    val createdAt: Long? = null
)

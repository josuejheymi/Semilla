package com.yey.semilla.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val photoUri: String? = null,
    val fechanacimiento: Long,
    val genero: String,
    val peso: Double,
    val altura: Double,
    val createdAt: Long = System.currentTimeMillis()
) {
}
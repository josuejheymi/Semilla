package com.yey.semilla.data.remote.dto

data class ReminderNetworkDto(
    val id: Long? = null,
    val userId: Int,
    val medicationId: Int,
    val startDate: Long,
    val endDate: Long?,
    val timesPerDay: Int,
    val time: String,
    val isEnabled: Boolean
)


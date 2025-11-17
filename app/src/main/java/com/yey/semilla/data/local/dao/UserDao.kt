package com.yey.semilla.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yey.semilla.data.local.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<UserEntity>>
}
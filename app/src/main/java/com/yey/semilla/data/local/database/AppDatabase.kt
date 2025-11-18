package com.yey.semilla.data.local.database

import android.content.Context
import androidx.room.*
import com.yey.semilla.data.local.converters.Converters
import com.yey.semilla.data.local.dao.*
import com.yey.semilla.data.local.model.*

@Database(
    entities = [
        UserEntity::class,
        MedicationEntity::class,
        ReminderEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun medicationDao(): MedicationDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medicapp.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

package com.yey.semilla.data.local.database

import android.content.Context
import androidx.room.*
import com.yey.semilla.data.local.converters.Converters
import com.yey.semilla.data.local.dao.*
import com.yey.semilla.data.local.model.*
/**
 * Clase principal de la base de datos de Room.
 * * 1. Configuración: Define las entidades (tablas) del esquema (User, Medication, Reminder)
 * y la versión de la DB (v1).
 * 2. Type Converters: Declara el uso de Converters para manejar tipos no nativos (como Boolean a Int).
 * 3. Acceso DAO: Proporciona las interfaces DAO para interactuar con cada tabla.
 * 4. Patrón Singleton: Utiliza el bloque 'companion object' para asegurar que solo exista una
 * única instancia de la base de datos en toda la aplicación (Thread-safe).
 */
@Database(
    entities = [
        UserEntity::class,
        MedicationEntity::class,
        ReminderEntity::class
    ],
    version = 1,
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
                ).build().also { INSTANCE = it }
            }
        }
    }
}
package com.yey.semilla.domain.repository

import com.yey.semilla.domain.model.UserEntity
import kotlinx.coroutines.flow.Flow
/**
 * 🧱 PATRÓN REPOSITORIO: CONTRATO DE LA CAPA DE DOMINIO 🛡️
 *
 * Propósito: Esta interfaz define el 'contrato' o conjunto de reglas para manejar
 * los datos de la entidad User. Es la definición pura de lo que la aplicación
 * necesita hacer con los datos (ej: añadir, leer), sin importar cómo lo hace.
 *
 * 1. Aislamiento y Abstracción: El Repositorio aísla el resto de la aplicación (los ViewModels
 * y la lógica de negocio) de la fuente de datos subyacente (Room, Firestore, una API REST).
 * Si decidimos cambiar de Room a Firebase, solo tendríamos que modificar la implementación
 * de esta interfaz, sin tocar la lógica del ViewModel.
 *
 * 2. Manejo de Concurrencia: Utiliza Corrutinas para manejar operaciones asíncronas:
 * - 'suspend fun': Para operaciones de un solo disparo que bloquean (Insertar, Borrar, Actualizar).
 * - 'Flow': Para operaciones que requieren observar cambios en tiempo real (Listar datos).
 *
 * MÉTODOS MÍNIMOS Y SUGERENCIAS ADICIONALES:
 * ----------------------------------------
 * | Método Actual | Función |
 * |---------------|---------|
 * | addUser       | Crea un nuevo registro de usuario. |
 * | getAllUsers   | Obtiene una lista que se actualiza automáticamente. |
 *
 * | Sugerencia Adicional | Propósito |
 * |----------------------|-----------|
 * | getUserByEmail()     | Lógica de inicio de sesión o verificación. |
 * | updateUser()         | Modificar datos del usuario (ej: foto o contraseña). |
 * | deleteUser()         | Eliminar un usuario de la base de datos. |
 */
interface UserRepository {
    //Registrar
    suspend fun addUser(user: UserEntity)
    //Obtener Lista
    fun getAllUsers(): Flow<List<UserEntity>>

    // lOGINN -
    suspend fun login(email: String, password: String): UserEntity?
    suspend fun updateUser(user: UserEntity)

}
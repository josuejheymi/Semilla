package com.yey.semilla.domain.repository

import com.yey.semilla.data.local.dao.UserDao
import com.yey.semilla.data.local.model.UserEntity
import kotlinx.coroutines.flow.Flow
/**
 * 🛠️ IMPLEMENTACIÓN DEL REPOSITORIO (Capa de Datos)
 * * Propósito: Esta clase implementa el contrato 'UserRepository' y conecta la lógica de la app
 * con la fuente de datos local (Room).
 * * Contenido: Aquí se traduce la petición abstracta del dominio (ej: 'addUser')
 * a la acción concreta del DAO de Room (ej: 'userDao.insert()').
 * * Inyección: Recibe el 'UserDao' a través del constructor (Inyección de Dependencias).
 * * Extensión: Si la app usara datos remotos (API REST), esta clase manejaría la
 * lógica para decidir si obtener los datos de Room, de la red, o sincronizar ambos.
 */
class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    // ... implementación de los métodos del contrato ...
    override suspend fun addUser(user: UserEntity) {
        userDao.insert(user)
    }

    override fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAll()
    }
}

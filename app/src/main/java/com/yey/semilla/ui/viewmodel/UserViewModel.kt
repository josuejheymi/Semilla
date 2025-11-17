package com.yey.semilla.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yey.semilla.data.local.model.UserEntity
import com.yey.semilla.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
/**
 * 💡 VIEW MODEL DE USUARIOS (Capa de Presentación)
 *
 * Propósito: Gestionar la UI y la lógica de presentación relacionada con los datos del usuario.
 * Actúa como intermediario entre la Vista (UI) y el Repositorio de Usuarios.
 *
 * 1. Estado: Utiliza 'StateFlow' (users) para exponer la lista de usuarios. La UI observa este
 * flujo de datos para actualizarse automáticamente (reactividad).
 *
 * 2. Carga Inicial (init/loadUsers): Al inicializarse, el ViewModel lanza una corrutina
 * para recolectar continuamente ('collect') todos los usuarios del repositorio.
 *
 * 3. Operaciones: La función 'addUser' recibe todos los datos de registro (incluyendo campos de
 * salud como peso y altura), construye la 'UserEntity' y delega la inserción asíncrona al repositorio.
 */
class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _users = MutableStateFlow<List<UserEntity>>(emptyList())
    val users: StateFlow<List<UserEntity>> = _users.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            repository.getAllUsers().collect { list ->
                _users.value = list
            }
        }
    }

    fun addUser(
        name: String,
        email: String,
        password: String,
        fechanacimiento: Long,
        genero: String,
        peso: Double,
        altura: Double,
        photoUri: String? = null
    ) {
        viewModelScope.launch {
            val user = UserEntity(
                name = name,
                email = email,
                password = password,
                fechanacimiento = fechanacimiento,
                genero = genero,
                peso = peso,
                altura = altura,
                photoUri = photoUri
            )
            repository.addUser(user)
        }
    }
}
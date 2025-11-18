package com.yey.semilla.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yey.semilla.data.local.model.UserEntity
import com.yey.semilla.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    // Lista de usuarios (opcional si lo necesitas)
    private val _users = MutableStateFlow<List<UserEntity>>(emptyList())
    val users: StateFlow<List<UserEntity>> = _users.asStateFlow()

    // Usuario actualmente logueado
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Resultado del login
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

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

    // ---------------------------
    //      REGISTRAR USUARIO
    // ---------------------------
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

    // ---------------------------
    //            LOGIN
    // ---------------------------
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.login(email, password)
            _loginSuccess.value = user != null
            _currentUser.value = user
        }
    }

    // ---------------------------
    //           LOGOUT
    // ---------------------------
    fun logout() {
        _currentUser.value = null
        _loginSuccess.value = false
    }
}

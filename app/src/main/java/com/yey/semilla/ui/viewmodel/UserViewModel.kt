package com.yey.semilla.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yey.semilla.data.remote.RetrofitClient
import com.yey.semilla.data.remote.dto.UserNetworkDto
import com.yey.semilla.domain.model.UserEntity
import com.yey.semilla.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException   // 👈 IMPORTANTE

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    // Lista de usuarios (se va a llenar con local y/o backend)
    private val _users = MutableStateFlow<List<UserEntity>>(emptyList())
    val users: StateFlow<List<UserEntity>> = _users.asStateFlow()

    // Usuario actualmente logueado
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Resultado del login
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    // Estado para mostrar carga / errores
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadLocalUsers()
        refreshUsersFromBackend()
    }

    // =========================
    //     LOCAL (ROOM)
    // =========================

    private fun loadLocalUsers() {
        viewModelScope.launch {
            repository.getAllUsers().collect { list ->
                _users.value = list
            }
        }
    }

    fun updateUser(user: UserEntity) = viewModelScope.launch {
        repository.updateUser(user)
        _currentUser.value = user
    }

    // =========================
    //     REGISTRO (BACKEND + ROOM)
    // =========================
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
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val now = System.currentTimeMillis()

                // DTO para la API (sin id)
                val dto = UserNetworkDto(
                    id = null,
                    name = name,
                    email = email,
                    password = password,
                    photoUri = photoUri,
                    fechanacimiento = fechanacimiento,
                    genero = genero,
                    peso = peso,
                    altura = altura,
                    createdAt = now
                )

                // Llamada HTTP
                val created = RetrofitClient.api.createUser(dto)

                Log.d("UserViewModel", "Usuario creado en backend: $created")

                // Convertimos a entidad de Room
                val localUser = UserEntity(
                    id = created.id?.toInt() ?: 0,   // usamos el id del backend, // Room genera su propio ID
                    name = created.name,
                    email = created.email,
                    password = created.password,
                    fechanacimiento = created.fechanacimiento,
                    genero = created.genero,
                    peso = created.peso,
                    altura = created.altura,
                    photoUri = created.photoUri,
                    createdAt = created.createdAt ?: now
                )

                // Guardar en Room
                repository.addUser(localUser)

                // Opcional: loguear al usuario inmediatamente
                _currentUser.value = localUser
                _loginSuccess.value = true

            } catch (e: HttpException) {
                // 👇 OJO: SIEMPRE usar e.response() y e.code()
                val errorBody = try {
                    e.response()?.errorBody()?.string()
                } catch (_: Exception) {
                    null
                }

                Log.e(
                    "UserViewModel",
                    "Error creando usuario en backend: HTTP ${e.code()} body=$errorBody",
                    e
                )

                _errorMessage.value = when (e.code()) {
                    409 -> "El correo ya está registrado."
                    else -> "Error del servidor (${e.code()}) al crear usuario."
                }

            } catch (e: Exception) {
                Log.e("UserViewModel", "Error creando usuario en backend", e)
                _errorMessage.value = e.message ?: "Error inesperado al crear usuario."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // =========================
    //         LOGIN
    // =========================

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // 1) Probar login remoto simple (buscar por email)
                val remoteUsers = RetrofitClient.api.getUsers()
                val found = remoteUsers.find { it.email == email && it.password == password }

                if (found != null) {
                    // Mapear a UserEntity local (usamos el id del backend)
                    val localUser = UserEntity(
                        id = found.id?.toInt() ?: 0,
                        name = found.name,
                        email = found.email,
                        password = found.password,
                        fechanacimiento = found.fechanacimiento,
                        genero = found.genero,
                        peso = found.peso,
                        altura = found.altura,
                        photoUri = found.photoUri,
                        createdAt = found.createdAt
                    )

                    // ❌ OJO: ya NO lo guardamos otra vez en Room para evitar conflicto
                    // repository.addUser(localUser)

                    _currentUser.value = localUser
                    _loginSuccess.value = true
                    return@launch
                }

                // 2) Si no está en remoto, probar login local
                val local = repository.login(email, password)
                _loginSuccess.value = local != null
                _currentUser.value = local

            } catch (e: Exception) {
                Log.e("UserViewModel", "Error en login", e)
                _errorMessage.value = e.message ?: "Error inesperado en login."
                _loginSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    // =========================
    //        LOGOUT
    // =========================

    fun logout() {
        _currentUser.value = null
        _loginSuccess.value = false
    }

    // =========================
    //     REFRESH USERS
    // =========================

    fun refreshUsersFromBackend() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val remoteUsers = RetrofitClient.api.getUsers()
                _users.value = remoteUsers

            } catch (e: Exception) {
                Log.e("UserViewModel", "Error al obtener usuarios del backend", e)
                _errorMessage.value = e.message ?: "Error inesperado al cargar usuarios"
            } finally {
                _isLoading.value = false
            }
        }
    }

    //====================
    //     ACTUALIZAR USUARIO
    //====================
    fun updateProfile(
        name: String,
        genero: String,
        peso: Double,
        altura: Double,
        fechanacimiento: Long,
        photoUri: String?
    ) {
        viewModelScope.launch {
            val current = _currentUser.value ?: return@launch

            try {
                _isLoading.value = true
                _errorMessage.value = null

                // DTO para enviar al backend
                val dto = UserNetworkDto(
                    id = current.id.toLong(),            //  id del backend
                    name = name,
                    email = current.email,              // normalmente no lo cambias aquí
                    password = current.password,        // igual
                    photoUri = photoUri,
                    fechanacimiento = fechanacimiento,
                    genero = genero,
                    peso = peso,
                    altura = altura,
                    createdAt = current.createdAt
                )

                // Llamada PUT /api/users/{id}
                val updated = RetrofitClient.api.updateUser(current.id.toLong(), dto)

                // Mapear de vuelta a entidad local
                val localUpdated = UserEntity(
                    id = updated.id?.toInt() ?: current.id,
                    name = updated.name,
                    email = updated.email,
                    password = updated.password,
                    fechanacimiento = updated.fechanacimiento,
                    genero = updated.genero,
                    peso = updated.peso,
                    altura = updated.altura,
                    photoUri = updated.photoUri,
                    createdAt = updated.createdAt ?: current.createdAt
                )

                // Actualizar en Room y en memoria
                repository.updateUser(localUpdated)
                _currentUser.value = localUpdated

            } catch (e: HttpException) {
                val body = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
                Log.e("UserViewModel", "Error actualizando perfil: HTTP ${e.code()} body=$body", e)
                _errorMessage.value = "Error al actualizar perfil (${e.code()})"
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error actualizando perfil", e)
                _errorMessage.value = e.message ?: "Error inesperado al actualizar perfil."
            } finally {
                _isLoading.value = false
            }
        }
    }



}

package com.danish.noorservice.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.User
import com.danish.noorservice.data.repository.AuthRepository
import com.danish.noorservice.data.repository.AuthResult
import com.danish.noorservice.data.repository.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isCheckingAuth: Boolean = true,  // true until Firebase confirms auth state
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val selectedRole: String = "",
    val error: String? = null
)

sealed class AuthEvent {
    data object LoginSuccess : AuthEvent()
    data object SignupSuccess : AuthEvent()
    data class Error(val message: String) : AuthEvent()
    data object LogoutSuccess : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authStateChanges().collect { authState ->
                when (authState) {
                    is AuthState.Loading -> {
                        _uiState.value = _uiState.value.copy(isCheckingAuth = true)
                    }
                    is AuthState.Unauthenticated -> {
                        _uiState.value = _uiState.value.copy(
                            isCheckingAuth = false,
                            currentUser = null
                        )
                    }
                    is AuthState.Authenticated -> {
                        _uiState.value = _uiState.value.copy(
                            isCheckingAuth = false,
                            currentUser = authState.user
                        )
                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.login(email, password)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = result.user
                    )
                    _events.emit(AuthEvent.LoginSuccess)
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                    _events.emit(AuthEvent.Error(result.message))
                }
            }
        }
    }

    fun signup(email: String, password: String, role: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.signup(email, password, role)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = result.user,
                        selectedRole = role
                    )
                    _events.emit(AuthEvent.SignupSuccess)
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                    _events.emit(AuthEvent.Error(result.message))
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState(isCheckingAuth = false)
            _events.emit(AuthEvent.LogoutSuccess)
        }
    }

    fun setSelectedRole(role: String) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    fun getCurrentUserUid(): String? = authRepository.getCurrentUserUid()
}
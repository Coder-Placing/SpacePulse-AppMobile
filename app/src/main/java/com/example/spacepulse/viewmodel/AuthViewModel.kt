package com.example.spacepulse.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacepulse.model.beans.LoginRequest
import com.example.spacepulse.model.beans.RegisterRequest
import com.example.spacepulse.model.client.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<Result<String>?>(null)
    val loginState: StateFlow<Result<String>?> = _loginState

    private val _registerState = MutableStateFlow<Result<String>?>(null)
    val registerState: StateFlow<Result<String>?> = _registerState

    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = body.token
                    val fullName = body.fullName
                    val userEmail = body.email
                    val userId = body.userId

                    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("USER_TOKEN", token)
                        putString("USER_FULL_NAME", fullName)
                        putString("USER_EMAIL", userEmail)
                        putString("USER_ID", userId)
                        apply()
                    }

                    _loginState.value = Result.success("Login exitoso")
                } else {
                    _loginState.value = Result.failure(Exception("Credenciales incorrectas"))
                }
            } catch (e: Exception) {
                _loginState.value = Result.failure(e)
            }
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.register(request)
                if (response.isSuccessful && response.body() != null) {
                    _registerState.value = Result.success("Registro exitoso")
                } else {
                    _registerState.value = Result.failure(Exception("Error al registrar: Verifica los datos"))
                }
            } catch (e: Exception) {
                _registerState.value = Result.failure(e)
            }
        }
    }

    fun logout(context: Context) {
        val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
        _loginState.value = null
    }

    fun resetLoginState() { _loginState.value = null }
    fun resetRegisterState() { _registerState.value = null }
}
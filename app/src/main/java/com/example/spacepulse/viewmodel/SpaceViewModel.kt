package com.example.spacepulse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacepulse.model.beans.CreateSpaceRequest
import com.example.spacepulse.model.beans.SpaceResponse
import com.example.spacepulse.model.client.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SpaceViewModel : ViewModel() {

    private val _spaces = MutableStateFlow<List<SpaceResponse>>(emptyList())
    val spaces: StateFlow<List<SpaceResponse>> = _spaces

    private val _createSpaceState = MutableStateFlow<Result<String>?>(null)
    val createSpaceState: StateFlow<Result<String>?> = _createSpaceState

    private val _deleteSpaceState = MutableStateFlow<Result<String>?>(null)
    val deleteSpaceState: StateFlow<Result<String>?> = _deleteSpaceState

    fun fetchSpaces(token: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getSpaces("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val allSpaces = response.body()!!
                    _spaces.value = allSpaces.filter { it.homeownerId == userId }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun createSpace(token: String, userId: String, request: CreateSpaceRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.createSpace("Bearer $token", request)
                if (response.isSuccessful) {
                    _createSpaceState.value = Result.success("Espacio creado")
                    fetchSpaces(token, userId)
                } else {
                    _createSpaceState.value = Result.failure(Exception("Error al crear"))
                }
            } catch (e: Exception) {
                _createSpaceState.value = Result.failure(e)
            }
        }
    }

    fun deleteSpace(token: String, userId: String, id: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.deleteSpace("Bearer $token", id)
                if (response.isSuccessful) {
                    _deleteSpaceState.value = Result.success("Eliminado")
                    fetchSpaces(token, userId)
                } else {
                    _deleteSpaceState.value = Result.failure(Exception("Error al eliminar"))
                }
            } catch (e: Exception) {
                _deleteSpaceState.value = Result.failure(e)
            }
        }
    }

    fun resetStates() {
        _createSpaceState.value = null
        _deleteSpaceState.value = null
    }
}
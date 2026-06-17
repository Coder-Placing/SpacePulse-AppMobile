package com.example.spacepulse.viewmodel




import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacepulse.model.beans.CreateSpaceRequest
import com.example.spacepulse.model.beans.IoTDeviceResponse
import com.example.spacepulse.model.beans.NotificationResponse
import com.example.spacepulse.model.beans.RegisterRequest
import com.example.spacepulse.model.beans.SpaceResponse
import com.example.spacepulse.model.beans.TaskRequest
import com.example.spacepulse.model.beans.TaskResponse
import com.example.spacepulse.model.client.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpaceViewModel : ViewModel() {

    private val _tasksList = MutableStateFlow<List<TaskResponse>>(emptyList())
    val tasksList: StateFlow<List<TaskResponse>> = _tasksList.asStateFlow()

    private val _isLoadingTasks = MutableStateFlow(false)
    val isLoadingTasks: StateFlow<Boolean> = _isLoadingTasks.asStateFlow()


    private val _spaces = MutableStateFlow<List<SpaceResponse>>(emptyList())
    val spaces: StateFlow<List<SpaceResponse>> = _spaces

    private val _iotDevices = MutableStateFlow<List<IoTDeviceResponse>>(emptyList())
    val iotDevices: StateFlow<List<IoTDeviceResponse>> = _iotDevices

    private val _notifications = MutableStateFlow<List<NotificationResponse>>(emptyList())
    val notifications: StateFlow<List<NotificationResponse>> = _notifications

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

    fun fetchIoTDevices(token: String, spaceId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getIoTDevicesBySpace("Bearer $token", spaceId)
                if (response.isSuccessful) {
                    _iotDevices.value = response.body() ?: emptyList()
                } else {
                    _iotDevices.value = emptyList()
                }
            } catch (e: Exception) {
                _iotDevices.value = emptyList()
            }
        }
    }

    fun fetchNotifications(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getUserNotifications("Bearer $token")
                if (response.isSuccessful) {
                    _notifications.value = response.body() ?: emptyList()
                } else {
                    _notifications.value = emptyList()
                }
            } catch (e: Exception) {
                _notifications.value = emptyList()
            }
        }
    }

    fun createSpace(context: Context, imageUri: Uri?, token: String, userId: String, request: CreateSpaceRequest) {
        viewModelScope.launch {
            try {
                val photoUrl = if (imageUri != null) {
                    ImgBBUploader.uploadPhoto(context, imageUri) ?: ""
                } else {
                    ""
                }

                val imagesList = if (photoUrl.isNotEmpty()) listOf(photoUrl) else emptyList()

                val finalRequest = request.copy(images = imagesList)

                val response = RetrofitClient.webService.createSpace("Bearer $token", finalRequest)

                if (response.isSuccessful) {
                    _createSpaceState.value = Result.success("Espacio creado")
                    fetchSpaces(token, userId)
                } else {
                    _createSpaceState.value = Result.failure(Exception("Error al crear: ${response.code()}"))
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

    fun requestTask(token: String, request: TaskRequest, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.requestTask("Bearer $token", request)
                if (response.isSuccessful) {
                    onComplete()
                } else {
                    onComplete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete()
            }
        }
    }

    fun getTasksForSpace(token: String, spaceId: Long) {
        viewModelScope.launch {
            _isLoadingTasks.value = true
            try {
                val response = RetrofitClient.webService.getTasksBySpaceId("Bearer $token", spaceId)
                if (response.isSuccessful) {
                    _tasksList.value = response.body() ?: emptyList()
                } else {
                    android.util.Log.e("TASKS", "Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("TASKS", "Fallo de conexión: ${e.message}")
            } finally {
                _isLoadingTasks.value = false
            }
        }
    }

    fun deleteModelTask(token: String, taskId: Long, spaceId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.deleteWorkItem("Bearer $token", taskId)
                if (response.isSuccessful) {
                    getTasksForSpace(token, spaceId)
                } else {
                    android.util.Log.e("TASKS", "Error al eliminar: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("TASKS", "Fallo al eliminar: ${e.message}")
            }
        }
    }

    fun updateModelTask(token: String, taskId: Long, title: String, description: String, spaceId: Long) {
        viewModelScope.launch {
            try {
                val request = com.example.spacepulse.model.beans.UpdateTaskContentRequest(title, description, "")
                val response = RetrofitClient.webService.updateTaskContent("Bearer $token", taskId, request)
                if (response.isSuccessful) {
                    getTasksForSpace(token, spaceId)
                } else {
                    android.util.Log.e("TASKS", "Error al editar: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("TASKS", "Fallo al editar: ${e.message}")
            }
        }
    }

    fun registerUser(context: Context, imageUri: Uri?, email: String, pass: String, name: String, phone: String) {
        viewModelScope.launch {
            val photoUrl = if (imageUri != null) {
                ImgBBUploader.uploadPhoto(context, imageUri) ?: ""
            } else {
                ""
            }

            val registerRequest = RegisterRequest(
                email = email,
                password = pass,
                fullName = name,
                phone = phone,
                role = "Homeowner",
                photo = photoUrl
            )

            try {
                val response = RetrofitClient.webService.register(registerRequest)
            } catch (e: Exception) {
            }
        }



    }
}




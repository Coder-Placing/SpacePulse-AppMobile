package com.example.spacepulse.viewmodel




import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacepulse.model.beans.CreateSpaceRequest
import com.example.spacepulse.model.beans.IoTDeviceResponse
import com.example.spacepulse.model.beans.NotificationResponse
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

    fun markNotificationAsRead(token: String, notificationId: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.markNotificationAsRead(
                    "Bearer $token",
                    notificationId
                )

                if (response.isSuccessful) {
                    _notifications.value = _notifications.value.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(isRead = true)
                        } else {
                            notification
                        }
                    }
                } else {
                    android.util.Log.e(
                        "NOTIFICATIONS",
                        "Error al marcar como leída: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e(
                    "NOTIFICATIONS",
                    "Fallo al marcar como leída: ${e.message}"
                )
            } finally {
                onComplete()
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

}
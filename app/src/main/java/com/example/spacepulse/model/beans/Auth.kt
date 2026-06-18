package com.example.spacepulse.model.beans

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val role: String,
    val photo: String
)

data class LoginResponse(
    val userId: String,
    val fullName: String,
    val email: String,
    val token: String
)

data class RegisterResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String?,
    val role: String,
    val photo: String?,
    val createdAt: String?
)

data class PaymentMethodResponse(
    val id: String,
    val type: String,
    val number: String,
    val expiry: String,
    val cvv: String
)

data class UserProfileResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String?,
    val role: String,
    val photo: String?,
    val paymentMethods: List<PaymentMethodResponse>
)

data class AddPaymentMethodRequest(
    val type: String,
    val number: String,
    val expiry: String,
    val cvv: String
)

data class SpaceResponse(
    val id: Long,
    val title: String,
    val description: String,
    val location: String,
    val homeownerId: String?,
    val spaceType: String?,
    val dimensionsSquareMeters: Double?,
    val estimatedBudget: Double?,
    val currency: String?,
    val status: String,
    val hasIot: Boolean
)

data class CreateSpaceRequest(
    val homeownerId: String,
    val title: String,
    val description: String,
    val location: String,
    val spaceType: String,
    val dimensionsSquareMeters: Double,
    val estimatedBudget: Double,
    val currency: String,
    val hasIot: Boolean,
    val images: List<String>
)

data class IoTDeviceResponse(
    val id: Long,
    val spaceId: Long,
    val type: String,
    val name: String,
    val serialNumber: String
)

data class NotificationResponse(
    val id: Long? = null,
    val spaceId: Long? = null,
    val title: String? = null,
    val message: String? = null,
    val isRead: Boolean? = null,
    val createdAt: String? = null
)

data class TaskRequest(
    val spaceId: Long,
    val title: String,
    val description: String,
    val photoUrl: String
)

data class TaskResponse(
    val id: Long,
    val spaceId: Long,
    val title: String,
    val description: String,
    val photoUrl: String,
    val status: String
)

data class UpdateTaskContentRequest(
    val title: String,
    val description: String,
    val photoUrl: String = ""
)
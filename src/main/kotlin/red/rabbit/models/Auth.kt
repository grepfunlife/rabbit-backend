package red.rabbit.models

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val email: String,
    val password: String,
    val chatId: String?
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val chatId: String?
)

@Serializable
data class TokenResponse(
    val token: String?
)

@Serializable
data class ChangePasswordRequest(
    val email: String,
    val currentPassword: String,
    val newPassword: String
)

package red.rabbit.models

import kotlinx.serialization.Serializable

@Serializable
data class CredentialsRequest(
    val email: String,
    val password: String
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

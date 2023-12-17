package red.rabbit.models

import at.favre.lib.crypto.bcrypt.BCrypt
import kotlinx.serialization.Serializable

@Serializable
data class LoginRegister(
    val email: String,
    val password: String
) {
    fun hashedPassword(): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun isPasswordVerified(password: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
    }
}
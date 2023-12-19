package red.rabbit.utils

import at.favre.lib.crypto.bcrypt.BCrypt

class Crypt {
    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun isPasswordVerified(password: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
    }
}
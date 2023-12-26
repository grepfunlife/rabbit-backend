package red.rabbit.validators

import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.ValidationResult.*
import mu.KotlinLogging
import org.apache.commons.validator.routines.EmailValidator
import red.rabbit.models.ChangePasswordRequest
import red.rabbit.models.LoginRequest
import red.rabbit.models.RegistrationRequest
import red.rabbit.services.ProfileService
import red.rabbit.utils.Crypt


private val logger = KotlinLogging.logger {}

fun RequestValidationConfig.profileValidation() {

    val profileService = ProfileService()
    val crypt = Crypt()

    fun isValidEmail(email: String): Boolean {
        return EmailValidator.getInstance().isValid(email)
    }

    validate<RegistrationRequest> {
        val profile = profileService.getProfileByEmail(it.email)

        if (profile != null) {
            logger.info("Email ${it.email} is already in use")
            Invalid("Email ${it.email} is already in use")
        } else if (!isValidEmail(it.email)) {
            logger.info("Invalid email ${it.email}")
            Invalid("Invalid email ${it.email}")
        } else if (it.email.isBlank()) {
            Invalid("Email shouldn't be blank")
        } else if (it.password.isBlank() || it.password.length < 5) {
            Invalid("Password should be at least 5 characters long")
        } else Valid
    }

    validate<LoginRequest> {
        val profile = profileService.getProfileByEmail(it.email)

        if (profile == null) {
            Invalid("Account with email ${it.email} has not been found")
        } else if (it.email.isBlank()) {
            Invalid("Email shouldn't be blank")
        } else if (it.password.isBlank()) {
            Invalid("Password shouldn't be blank")
        } else if (!crypt.isPasswordVerified(it.password, profile.password)) {
            Invalid("Incorrect password")
        } else Valid
    }

    validate<ChangePasswordRequest> {
        val profile = profileService.getProfileByEmail(it.email)

        if (profile == null) {
            Invalid("Account with email ${it.email} has not been found")
        } else if (it.email.isBlank()) {
            Invalid("Email shouldn't be blank")
        } else if (it.newPassword.isBlank() || it.currentPassword.isBlank()) {
            Invalid("Password shouldn't be blank")
        } else if (!crypt.isPasswordVerified(it.currentPassword, profile.password)) {
            Invalid("Unable to update password. Passwords are not match")
        } else if (crypt.isPasswordVerified(it.newPassword, profile.password)) {
            Invalid("Unable to update password. New password matches old password")
        } else Valid
    }
}
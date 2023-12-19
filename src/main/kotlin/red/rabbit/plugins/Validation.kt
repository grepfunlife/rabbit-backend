package red.rabbit.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import red.rabbit.models.ChangePasswordRequest
import red.rabbit.models.CredentialsRequest

fun Application.configureValidation() {
    install(RequestValidation) {
        validate<CredentialsRequest> {
            if (it.email.isEmpty() || it.password.isEmpty()) {
                ValidationResult.Invalid("Empty email or password")
            } else ValidationResult.Valid
        }

        validate<ChangePasswordRequest>{
            if (it.email.isEmpty() || it.newPassword.isEmpty() || it.currentPassword.isEmpty()) {
                ValidationResult.Invalid("Empty email or password")
            } else ValidationResult.Valid
        }
    }
}

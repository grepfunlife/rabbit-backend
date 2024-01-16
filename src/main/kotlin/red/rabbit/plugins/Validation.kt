package red.rabbit.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import red.rabbit.validators.habitValidation
import red.rabbit.validators.profileValidation

fun Application.configureValidation() {
    install(RequestValidation) {
        profileValidation()
        habitValidation()
    }
}

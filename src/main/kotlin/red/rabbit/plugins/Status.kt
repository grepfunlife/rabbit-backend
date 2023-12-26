package red.rabbit.plugins

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatus() {
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.respond(BadRequest, "${cause.message?.replaceBefore("Reasons", "")}")
        }

        exception<Throwable> { call, cause ->
            call.respond(BadRequest, "${cause.message?.replaceBefore("Reasons", "")}")
        }

        exception<Exception> { call, cause ->
            call.respond(BadRequest, "Oops, something is wrong :(")
        }
    }
}
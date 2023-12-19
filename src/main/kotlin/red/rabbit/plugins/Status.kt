package red.rabbit.plugins

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import red.rabbit.models.BaseResponse

fun Application.configureStatus() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(BadRequest, BaseResponse("error", "${cause.message?.replaceBefore("Reasons", "")}"))
        }
    }
}
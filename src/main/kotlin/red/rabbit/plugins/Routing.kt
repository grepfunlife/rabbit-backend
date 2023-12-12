package red.rabbit.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import red.rabbit.routes.profileRouting

fun Application.configureRouting() {
    routing {
        profileRouting()
    }
}

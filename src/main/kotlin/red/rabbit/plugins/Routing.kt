package red.rabbit.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import red.rabbit.routes.eventRouting
import red.rabbit.routes.habitRouting
import red.rabbit.routes.profileRouting
import red.rabbit.routes.utilsRouting

fun Application.configureRouting() {
    routing {
        profileRouting()
        utilsRouting()
        habitRouting()
        eventRouting()
    }
}

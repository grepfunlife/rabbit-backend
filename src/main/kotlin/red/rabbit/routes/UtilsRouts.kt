package red.rabbit.routes

import io.ktor.server.routing.*
import io.ktor.server.plugins.swagger.*

fun Route.utilsRouting() {
    route("/") {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }
}
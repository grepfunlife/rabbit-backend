package red.rabbit

import io.ktor.server.application.*
import red.rabbit.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSecurity()
    configureSerialization()
    configureRouting()
    configureValidation()
    configureStatus()
}

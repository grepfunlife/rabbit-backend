package red.rabbit

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain.main
import red.rabbit.plugins.*

fun main(args: Array<String>) {
    main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSecurity()
    configureSerialization()
    configureRouting()
    configureValidation()
    configureStatus()
}

package red.rabbit.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.response.*

fun Application.configureSecurity() {

    val appConfig = HoconApplicationConfig(ConfigFactory.load())

    val jwtSecret = appConfig.property("jwt.secret").getString()
    val jwtIssuer = appConfig.property("jwt.issuer").getString()
    val jwtRealm = appConfig.property("jwt.realm").getString()

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.issuer.contains(jwtIssuer)) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}

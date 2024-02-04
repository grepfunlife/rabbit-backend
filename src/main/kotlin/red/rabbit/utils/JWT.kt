package red.rabbit.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import java.lang.System.currentTimeMillis
import java.util.*

const val VALIDITY_IN_MS = 315360000000 // 1 day

object JWT {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())

    private val jwtSecret = appConfig.property("jwt.secret").getString()
    private val jwtIssuer = appConfig.property("jwt.issuer").getString()

    fun createJwtToken(email: String): String? {
        return JWT.create()
            .withIssuer(jwtIssuer)
            .withClaim("email", email)
            .withExpiresAt(Date(currentTimeMillis() + VALIDITY_IN_MS))
            .sign(Algorithm.HMAC256(jwtSecret))
    }
}
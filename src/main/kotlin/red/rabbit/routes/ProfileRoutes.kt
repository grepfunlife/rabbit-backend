package red.rabbit.routes

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.AuthenticationStrategy.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import red.rabbit.models.ChangePasswordRequest
import red.rabbit.models.LoginRequest
import red.rabbit.models.RegistrationRequest
import red.rabbit.models.TokenResponse
import red.rabbit.services.ProfileService
import red.rabbit.utils.Crypt
import red.rabbit.utils.JWT

val profileService = ProfileService()
val crypt = Crypt()

private val logger = KotlinLogging.logger {}

fun Route.profileRouting() {

    route("/auth") {

        post("/register") {

            val credentials = call.receive<RegistrationRequest>()
            val hashedPassword = crypt.hashPassword(credentials.password)

            logger.info("Registration user with email ${credentials.email}")
            profileService.registerProfile(credentials.email, hashedPassword)
            call.respond(OK, "Registration is successful")
        }

        post("/login") {
            val credentials = call.receive<LoginRequest>()
            val profile = profileService.getProfileByEmail(credentials.email)
            logger.info("Login by user with email ${credentials.email}")
            val token = JWT.createJwtToken(profile!!.email)
            logger.info("Response with token")
            call.respond(OK, TokenResponse(token))
        }

        authenticate("auth-jwt", strategy = Required) {
            post("/changePassword") {

                val credentials = call.receive<ChangePasswordRequest>()
                val hashedPassword = crypt.hashPassword(credentials.newPassword)
                logger.info("Changing password by user with email ${credentials.email}")

                profileService.updatePassword(credentials.email, hashedPassword)
                call.respond(OK, "Password has been updated")
            }
        }
    }
}

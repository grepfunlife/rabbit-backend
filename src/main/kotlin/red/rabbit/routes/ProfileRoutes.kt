package red.rabbit.routes

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.AuthenticationStrategy.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import red.rabbit.models.ChangePasswordRequest
import red.rabbit.models.LoginRequest
import red.rabbit.models.RegistrationRequest
import red.rabbit.models.TokenResponse
import red.rabbit.services.ProfileService
import red.rabbit.utils.Crypt
import red.rabbit.utils.JWT

val profileService = ProfileService()
val crypt = Crypt()

fun Route.profileRouting() {

    route("/auth") {

        post("/registration") {
            val credentials = call.receive<RegistrationRequest>()
            val hashedPassword = crypt.hashPassword(credentials.password)
            application.log.info("Registration user with email ${credentials.email}")
            profileService.registrationProfile(credentials.email, hashedPassword, credentials.chatId)
            call.respond(OK, "Registration is successful")
        }

        post("/login") {
            val credentials = call.receive<LoginRequest>()
            val profile = profileService.getProfileByEmail(credentials.email)
            application.log.info("Login by user with email ${credentials.email}")
            val token = JWT.createJwtToken(profile!!.email)
            profileService.addTokenToProfile(credentials.email, token!!)
            application.log.info("Token has been created")
            call.respond(OK, "Login is successful")
        }

        get("/isChatIdExists") {
            application.log.info("Get existing chat id")
            val chatId = call.parameters.getOrFail<String>("chatId")
            val message = profileService.isChatIdExits(chatId)
            call.respond(OK, message)
        }

        get("/getTokenByChatId") {
            val chatId = call.parameters.getOrFail<String>("chatId")
            application.log.info("Get token by chat id $chatId")
            val token = profileService.getTokenByChatId(chatId)
            call.respond(OK, TokenResponse(token))
        }

        authenticate("auth-jwt", strategy = Required) {
            post("/changePassword") {
                val credentials = call.receive<ChangePasswordRequest>()
                val hashedPassword = crypt.hashPassword(credentials.newPassword)
                application.log.info("Changing password by user with email ${credentials.email}")
                profileService.updatePassword(credentials.email, hashedPassword)
                call.respond(OK, "Password has been updated")
            }
        }
    }
}


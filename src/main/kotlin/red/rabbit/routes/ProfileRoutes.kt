package red.rabbit.routes

import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import red.rabbit.models.BaseResponse
import red.rabbit.models.auth.RequestCredentials
import red.rabbit.models.auth.ResponseToken
import red.rabbit.services.ProfileService
import red.rabbit.utils.JWT

fun Route.profileRouting() {

    route("/auth") {
        val profileService = ProfileService()

        authenticate("auth-jwt", strategy = AuthenticationStrategy.Required) {
            get("/test") {
                call.respond(OK, BaseResponse("success", "Auth 2.1"))
            }
        }

        post("/register") {
            val credentials = call.receive<RequestCredentials>()
            val hashedPassword = credentials.hashedPassword()
            call.application.log.info("Registration user with email ${credentials.email}")
            profileService.registerProfile(credentials.email, hashedPassword)
            call.respond(OK, BaseResponse("success", "Registration is successful"))
        }

        post("/login") {
            val credentials = call.receive<RequestCredentials>()
            val profile = profileService.getProfileByEmail(credentials.email)
            call.application.log.info("Login by user with email ${credentials.email}")
            if (profile == null || !credentials.isPasswordVerified(credentials.password, profile.password)) {
                call.respond(Forbidden, BaseResponse("error", "Invalid Credentials"))
            }
            val token = JWT.createJwtToken(profile!!.email)
            call.application.log.info("Response with token")
            call.respond(OK, ResponseToken(BaseResponse("success", "Token has been created"), token))
        }

        post("/changePassword") {
            val credentials = call.receive<RequestCredentials>()
        }
    }
}

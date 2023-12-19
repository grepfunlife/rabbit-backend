package red.rabbit.routes

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.AuthenticationStrategy.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import red.rabbit.models.BaseResponse
import red.rabbit.models.ChangePasswordRequest
import red.rabbit.models.CredentialsRequest
import red.rabbit.models.TokenResponse
import red.rabbit.services.ProfileService
import red.rabbit.utils.Crypt
import red.rabbit.utils.JWT

fun Route.profileRouting() {

    route("/auth") {
        val profileService = ProfileService()
        val crypt = Crypt()

        post("/register") {
            val credentials = call.receive<CredentialsRequest>()
            val hashedPassword = crypt.hashPassword(credentials.password)
            call.application.log.info("Registration user with email ${credentials.email}")
            profileService.registerProfile(credentials.email, hashedPassword)
            call.respond(OK, BaseResponse("success", "Registration is successful"))
        }

        post("/login") {
            val credentials = call.receive<CredentialsRequest>()
            val profile = profileService.getProfileByEmail(credentials.email)
            call.application.log.info("Login by user with email ${credentials.email}")
            if (profile == null || !crypt.isPasswordVerified(credentials.password, profile.password)) {
                call.respond(Forbidden, BaseResponse("error", "Invalid Credentials"))
            }
            val token = JWT.createJwtToken(profile!!.email)
            call.application.log.info("Response with token")
            call.respond(OK, TokenResponse(BaseResponse("success", "Token has been created"), token))
        }

        authenticate("auth-jwt", strategy = Required) {
            post("/changePassword") {
                val credentials = call.receive<ChangePasswordRequest>()
                val hashedPassword = crypt.hashPassword(credentials.newPassword)
                val profile = profileService.getProfileByEmail(credentials.email)
                call.application.log.info("Changing password by user with email ${credentials.email}")

                if (profile == null || !crypt.isPasswordVerified(credentials.currentPassword, profile.password)) {
                    call.application.log.info("Unable to update password. Incorrect email or password")
                    call.respond(Forbidden, BaseResponse("error", "Invalid Credentials"))
                } else if (crypt.isPasswordVerified(credentials.newPassword, profile.password)) {
                    call.application.log.info("Unable to update password. New password matches old password")
                    call.respond(BadRequest, BaseResponse("error", "New password matches old password"))
                } else {
                    profileService.updatePassword(credentials.email, hashedPassword)
                    call.respond(OK, BaseResponse("success", "Password has been updated"))
                }
            }
        }
    }
}

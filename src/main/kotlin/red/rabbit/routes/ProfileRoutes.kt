package red.rabbit.routes

import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import red.rabbit.services.ProfileService
import red.rabbit.utils.JWT

fun Route.profileRouting() {
    @Serializable
    data class LoginRegister(val email: String, val password: String)

    route("/auth") {
        val profileService = ProfileService()

        authenticate("auth-jwt", strategy = AuthenticationStrategy.Required) {
            get("/test") {
                call.respondText("Success")
            }
        }

        post("/register") {
            val creds = call.receive<LoginRegister>()
            profileService.registerProfile(creds.email, creds.password)
            call.respond("Success!")
        }
        post("/login") {
            val creds = call.receive<LoginRegister>()
            val profile = profileService.getProfileByEmail(creds.email)
            if (profile == null || creds.password != profile.password) {
                call.respond(status = Forbidden, message = "Invalid Credentials")
            }
            val token = JWT.createJwtToken(profile!!.email)
            call.respond(hashMapOf("token" to token))
        }
    }
}

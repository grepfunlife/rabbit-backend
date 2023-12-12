package red.rabbit.routes


import red.rabbit.utils.JWT
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import red.rabbit.services.ProfileService


fun Route.profileRouting() {

    data class LoginRegister(val email: String, val password: String)

    route("/auth") {
        val profileService = ProfileService()

        get("/test") {
            call.respondText("Test auth 2.0")
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
                error("Invalid Credentials")
            }
            val token = JWT.createJwtToken(profile.email)
            call.respond(hashMapOf("token" to token))
        }
    }
}
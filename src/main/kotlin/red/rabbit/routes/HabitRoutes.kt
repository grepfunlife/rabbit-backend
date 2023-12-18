package red.rabbit.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import red.rabbit.services.HabitService

fun Route.habitRouting() {

    @Serializable
    data class HabitType(val name: String, val isGood: Boolean)

    route("/habits") {
        val habitService = HabitService()

        post("/add") {
            val habit = call.receive<HabitType>()
            habitService.addNewHabit(habit.name, habit.isGood)
            call.respondText("OK", status = HttpStatusCode.Created)
        }
    }
}
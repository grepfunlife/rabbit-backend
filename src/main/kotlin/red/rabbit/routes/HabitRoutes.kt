package red.rabbit.routes

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import red.rabbit.models.habit.HabitRequest
import red.rabbit.models.habit.HabitResponse
import red.rabbit.services.HabitService

fun Route.habitRouting() {

    route("/habits") {
        val habitService = HabitService()

        get() {
            call.application.log.info("Getting all habits")
            val habits = habitService.allHabits()
            call.respond(OK, habits)
        }

        post("/add") {
            val habitRequest = call.receive<HabitRequest>()
            call.application.log.info("Creating habit with name ${habitRequest.name}")
            val habit = habitService.addNewHabit(habitRequest.name, habitRequest.isGood)
            call.respond(OK, HabitResponse(habitId = habit?.id))
        }

        get("{id?}") {
            val id = call.parameters.getOrFail<Int>("id").toInt()
            val habit = habitService.habit(id)
            call.respond(OK, HabitResponse(habitIsGood = habit?.isGood, habitName = habit?.name))
        }
    }
}
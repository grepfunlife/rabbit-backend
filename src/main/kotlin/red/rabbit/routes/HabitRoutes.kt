package red.rabbit.routes

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import red.rabbit.models.habit.HabitRequest
import red.rabbit.models.habit.HabitResponse
import red.rabbit.services.HabitService

fun Route.habitRouting() {

    authenticate("auth-jwt", strategy = AuthenticationStrategy.Required) {
        route("/habits") {
            val habitService = HabitService()

            get {
                call.application.log.info("Getting all habits")
                val habits = habitService.allHabits()
                call.respond(OK, habits)
            }

            post("add") {
                val habitRequest = call.receive<HabitRequest>()
                call.application.log.info("Creating new habit $habitRequest")
                val habit = habitService.addNewHabit(habitRequest.name, habitRequest.isGood)
                call.application.log.info(habit.toString())
                call.respond(OK, HabitResponse(id = habit?.id))
            }


            get("{id?}") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.application.log.info("Getting habit with id $id")
                val habit = habitService.habit(id)
                if (habit != null)
                    call.respond(OK, HabitResponse(isGood = habit.isGood, name = habit.name))
                else
                    call.respond(BadRequest, "Habit with id = $id does not exist")
            }

            post("edit") {
                val habitRequest = call.receive<HabitRequest>()
                call.application.log.info("Editing habit with id ${habitRequest.id}")
                val status = habitService.editHabit(habitRequest.id, habitRequest.name, habitRequest.isGood)
                if (status)
                    call.respond(OK, status)
                else
                    call.respond(BadRequest, status)
            }

            post("delete/{id?}") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.application.log.info("Deleting habit with id $id}")
                val status = habitService.deleteHabit(id)
                if (status)
                    call.respond(OK, status)
                else
                    call.respond(BadRequest, status)
            }
        }
    }
}

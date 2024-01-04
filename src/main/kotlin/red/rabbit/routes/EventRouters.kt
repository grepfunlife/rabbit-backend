package red.rabbit.routes

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.datetime.toLocalDate
import red.rabbit.models.event.EventRequest
import red.rabbit.models.event.EventRequestBulk
import red.rabbit.models.event.EventResponse
import red.rabbit.services.EventService

fun Route.eventRouting() {

    authenticate("auth-jwt", strategy = AuthenticationStrategy.Required) {
        route("/events") {
            val eventService = EventService()

            get {
                call.application.log.info("Getting all events")
                val events = eventService.allEvents()
                call.respond(OK, events)
            }

            post("add") {
                val eventRequest = call.receive<EventRequest>()
                call.application.log.info("Creating event with habit id ${eventRequest.habitId}")
                val event = eventService.addNewEvent(eventRequest.date, eventRequest.habitId)
                call.application.log.info(event.toString())
                call.respond(OK, EventResponse(id = event?.id))
            }


            post("add/bulk") {
                val eventRequestBulk = call.receive<EventRequestBulk>()
                call.application.log.info("Creating events with habit id ${eventRequestBulk.habitId}")
                val events = eventService.addNewEventsBulk(eventRequestBulk.dates, eventRequestBulk.habitId)
                call.respond(OK, events)
            }

            post("edit") {
                val eventRequest = call.receive<EventRequest>()
                call.application.log.info("Editing event with id ${eventRequest.id}")
                val status = eventService.editEvent(eventRequest.id, eventRequest.date, eventRequest.habitId)
                if (status)
                    call.respond(OK, status)
                else
                    call.respond(BadRequest, status)
            }

            post("delete/{id?}") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.application.log.info("Deleting event with id $id}")
                val status = eventService.deleteEvent(id)
                if (status)
                    call.respond(OK, status)
                else
                    call.respond(BadRequest, status)
            }

            get("{date?}") {
                val date = call.parameters.getOrFail<String>("date")
                call.application.log.info("Getting events with date $date")
                val events = eventService.getEventsByDate(date.toLocalDate())
                call.respond(OK, events)
            }
        }
    }
}
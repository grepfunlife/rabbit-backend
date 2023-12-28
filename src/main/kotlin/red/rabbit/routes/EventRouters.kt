package red.rabbit.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import red.rabbit.models.event.EventRequest
import red.rabbit.models.event.EventResponse
import red.rabbit.services.EventService

fun Route.eventRouting() {

    authenticate("auth-jwt", strategy = AuthenticationStrategy.Required) {
        route("/events") {
            val eventService = EventService()

            get {
                call.application.log.info("Getting all events")
                val events = eventService.allEvents()
                call.respond(HttpStatusCode.OK, events)
            }

            post("add") {
                val eventRequest = call.receive<EventRequest>()
                call.application.log.info("Creating event with habit id ${eventRequest.habitId}")
                val event = eventService.addNewEvent(eventRequest.date, eventRequest.habitId)
                call.application.log.info(event.toString())
                call.respond(HttpStatusCode.OK, EventResponse(id = event?.id))
            }
        }
    }
}
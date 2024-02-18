package red.rabbit.routes

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.datetime.toLocalDate
import org.jetbrains.exposed.exceptions.ExposedSQLException
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
                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim("email").asString()
                val events = eventService.allEvents(email)
                call.respond(OK, events)
            }

            post("add") {
                val eventRequest = call.receive<EventRequest>()
                call.application.log.info("Creating new event $eventRequest")
                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim("email").asString()
                try {
                    val event = eventService.addNewEvent(eventRequest.date, eventRequest.habitId, email)
                    call.application.log.info(event.toString())
                    call.respond(OK, EventResponse(id = event?.id))
                } catch (e: ExposedSQLException) {
                    if (e.message!!.contains("already exists"))
                        call.respond(
                            BadRequest,
                            mapOf("result" to "This habit already exists for this date for this user")
                        )
                    else
                        call.respond(BadRequest, mapOf("result" to e.message.toString()))
                }

            }


            post("add/bulk") {
                val eventRequestBulk = call.receive<EventRequestBulk>()
                call.application.log.info("Creating events with habit id ${eventRequestBulk.habitId}")
                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim("email").asString()
                call.application.log.info(email)
                try {
                    val events = eventService.addNewEventsBulk(eventRequestBulk.dates, eventRequestBulk.habitId, email)
                    call.application.log.info(events.toString())
                    call.respond(OK, events)
                } catch (e: ExposedSQLException) {
                    if (e.message!!.contains("already exists"))
                        call.respond(
                            BadRequest,
                            mapOf("result" to "This habit already exists for all or any date for this user")
                        )
                    else
                        call.respond(BadRequest, mapOf("result" to e.message.toString()))
                }
            }

            post("edit") {
                val eventRequest = call.receive<EventRequest>()
                call.application.log.info("Editing event with id ${eventRequest.id}")
                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim("email").asString()
                val status = eventService.editEvent(eventRequest.id, eventRequest.date, eventRequest.habitId, email)
                if (status)
                    call.respond(OK, mapOf("result" to status))
                else
                    call.respond(BadRequest, mapOf("result" to status))
            }

            post("delete/{id?}") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.application.log.info("Deleting event with id $id}")
                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim("email").asString()
                val status = eventService.deleteEvent(id, email)
                if (status)
                    call.respond(OK, mapOf("result" to status))
                else
                    call.respond(BadRequest, mapOf("result" to status))
            }

            get("{date?}") {
                val date = call.parameters.getOrFail<String>("date")
                call.application.log.info("Getting events with date $date")
                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim("email").asString()
                val events = eventService.getEventsByDate(date.toLocalDate(), email)
                call.respond(OK, events)
            }
        }
    }
}

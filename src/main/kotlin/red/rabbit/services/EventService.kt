package red.rabbit.services

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import red.rabbit.DatabaseFactory.dbQuery
import red.rabbit.models.event.Event
import red.rabbit.models.event.Events


class EventService {

    val profileService = ProfileService()

    private fun resultRowToEvent(row: ResultRow): Event {
        return Event(
            id = row[Events.id],
            date = row[Events.date],
            habitId = row[Events.habitId],
            userId = row[Events.userId]
        )
    }

    suspend fun allEvents(email: String): List<Event> = dbQuery {
        exposedLogger.info("Select all events from DB")
        val userId = profileService.getUserIdByEmail(email)
        Events
            .select { Events.userId eq userId }
            .map(::resultRowToEvent)
    }

    suspend fun addNewEvent(date: LocalDate, habitId: Int, email: String): Event? = dbQuery {
        exposedLogger.info("Insert new event with habit id $habitId to DB")
        val userId = profileService.getUserIdByEmail(email)
        println("userId = $userId")
        val insertStatement = Events.insert {
            it[Events.date] = date
            it[Events.habitId] = habitId
            it[Events.userId] = userId
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToEvent)
    }

    suspend fun addNewEventsBulk(listOfDates: List<LocalDate>, habitId: Int, email: String): MutableList<Event?> {
        exposedLogger.info("Insert bulk events with habit id $habitId to DB")
        val eventsMutableList: MutableList<Event?> = mutableListOf()
        for (date in listOfDates) {
            eventsMutableList.add(dbQuery {
                val userId = profileService.getUserIdByEmail(email)
                println("userId = $userId")
                val insertStatement = Events.insert {
                    it[Events.date] = date
                    it[Events.habitId] = habitId
                    it[Events.userId] = userId
                }
                insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToEvent)
            })
        }
        return eventsMutableList
    }

    suspend fun editEvent(id: Int, date: LocalDate, habitId: Int, email: String): Boolean = dbQuery {
        exposedLogger.info("Update event with id $id and date $date to DB")
        val userId = profileService.getUserIdByEmail(email)
        Events.update({ (Events.id eq id) and (Events.userId eq userId) }) {
            it[Events.date] = date
            it[Events.habitId] = habitId
        } > 0
    }

    suspend fun deleteEvent(id: Int, email: String): Boolean = dbQuery {
        exposedLogger.info("Delete event with id $id from DB")
        val userId = profileService.getUserIdByEmail(email)
        Events.deleteWhere { (Events.userId eq userId) and (Events.id eq id) } > 0
    }

    suspend fun getEventsByDate(date: LocalDate, email: String): List<Event> {
        exposedLogger.info("Select events with date $date from DB")
        val events = dbQuery {
            val userId = profileService.getUserIdByEmail(email)
            Events
                .select { (Events.date eq date) and (Events.userId eq userId) }
                .map(::resultRowToEvent)
        }
        exposedLogger.info("Acquired $events from DB")
        return events
    }
}
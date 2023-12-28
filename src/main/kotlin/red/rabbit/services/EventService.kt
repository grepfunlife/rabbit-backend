package red.rabbit.services

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import red.rabbit.DatabaseFactory
import red.rabbit.models.event.Event
import red.rabbit.models.event.Events
import java.time.LocalDate

class EventService {
    private fun resultRowToEvent(row: ResultRow): Event =
        Event(
            id = row[Events.id],
            date = row[Events.date],
            habitId = row[Events.habitId]
        )

    suspend fun allEvents(): List<Event> = DatabaseFactory.dbQuery {
        exposedLogger.info("Select all events from DB")
        Events.selectAll().map(::resultRowToEvent)
    }

    suspend fun addNewEvent(date: LocalDate, habitId: Int): Event? = DatabaseFactory.dbQuery {
        exposedLogger.info("Insert new event with habit id $habitId to DB")
        val insertStatement = Events.insert {
            it[Events.date] = date
            it[Events.habitId] = habitId
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToEvent)
    }
}
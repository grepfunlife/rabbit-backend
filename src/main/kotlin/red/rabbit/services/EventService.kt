package red.rabbit.services

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import red.rabbit.DatabaseFactory.dbQuery
import red.rabbit.models.event.Event
import red.rabbit.models.event.Events


class EventService {


    private fun resultRowToEvent(row: ResultRow): Event {
        return Event(
            id = row[Events.id],
            date = row[Events.date],
            habitId = row[Events.habitId]
        )
    }

    suspend fun allEvents(): List<Event> = dbQuery {
        exposedLogger.info("Select all events from DB")
        Events.selectAll().map(::resultRowToEvent)
    }

    suspend fun addNewEvent(date: LocalDate, habitId: Int): Event? = dbQuery {
        exposedLogger.info("Insert new event with habit id $habitId to DB")
        val insertStatement = Events.insert {
            it[Events.date] = date
            it[Events.habitId] = habitId
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToEvent)
    }

    suspend fun editEvent(id: Int, date: LocalDate, habitId: Int): Boolean = dbQuery {
        exposedLogger.info("Update event with id $id and date $date to DB")
        Events.update({ Events.id eq id }) {
            it[Events.date] = date
            it[Events.habitId] = habitId
        } > 0
    }

    suspend fun deleteEvent(id: Int): Boolean = dbQuery {
        exposedLogger.info("Delete event with id $id from DB")
        Events.deleteWhere { Events.id eq id } > 0
    }

    suspend fun getEventsByDate(date: LocalDate): List<Event> {
        exposedLogger.info("Select events with date $date from DB")
        val events = dbQuery {
            Events
                .select { Events.date eq date }
                .map(::resultRowToEvent)
        }
        exposedLogger.info("Acquired $events from DB")
        return events
    }

    suspend fun addNewEventsBulk(listOfDates: List<LocalDate>, habitId: Int): MutableList<Event?> {
        exposedLogger.info("Insert bulk events with habit id $habitId to DB")
        val eventsMutableList: MutableList<Event?> = mutableListOf()
        for (date in listOfDates) {
            eventsMutableList.add(dbQuery {
                val insertStatement = Events.insert {
                    it[Events.date] = date
                    it[Events.habitId] = habitId
                }
                insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToEvent)
            })
        }
        return eventsMutableList
    }
}
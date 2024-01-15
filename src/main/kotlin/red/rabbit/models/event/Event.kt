package red.rabbit.models.event

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date
import red.rabbit.models.Profiles
import red.rabbit.models.habit.Habits


object Events : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val date: Column<LocalDate> = date("date")
    val habitId = integer("habit_id").uniqueIndex().references(Habits.id)
    val userId = integer("user_id").uniqueIndex().references(Profiles.id)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Event(
    val id: Int,
    val date: LocalDate,
    val habitId: Int,
    val userId: Int
)
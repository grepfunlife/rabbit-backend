package red.rabbit.models.event

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import red.rabbit.models.habit.Habits
import java.time.LocalDate

object Events : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val date = date("date")
    val habitId = integer("habit_id").uniqueIndex().references(Habits.id)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Event(
    val id: Int,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val habitId: Int
)
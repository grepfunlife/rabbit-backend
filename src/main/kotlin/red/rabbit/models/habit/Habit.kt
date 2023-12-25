package red.rabbit.models.habit

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Habits : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val isGood = bool("is_good")
    override val primaryKey = PrimaryKey(id)
}
@Serializable
data class Habit(
    val id: Int,
    val name: String,
    val isGood: Boolean
)
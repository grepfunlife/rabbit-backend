package red.rabbit.models

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Profiles : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val email = varchar("email", 100)
    val password = varchar("password", 100)
    val token = varchar("token", 300).nullable()
    val chatId = varchar("chat_id", 100).nullable()
    override val primaryKey = PrimaryKey(id)
}

data class Profile(
    val id: Int,
    val email: String,
    val password: String,
    val token: String?,
    val chatId: String?
)
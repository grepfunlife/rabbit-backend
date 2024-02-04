package red.rabbit.services

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import red.rabbit.DatabaseFactory.dbQuery
import red.rabbit.models.Profile
import red.rabbit.models.Profiles

class ProfileService {

    private fun resultRowToProfile(row: ResultRow): Profile =
        Profile(
            id = row[Profiles.id],
            email = row[Profiles.email],
            password = row[Profiles.password],
            token = row[Profiles.token],
            chatId = row[Profiles.chatId]
        )

    suspend fun getAllUsers(): List<Profile> = dbQuery {
        exposedLogger.info("Getting all users from DB")
        Profiles.selectAll().map { resultRowToProfile(it) }
    }

    suspend fun getProfileByEmail(email: String): Profile? = dbQuery {
        exposedLogger.info("Get profile data from DB by email $email")
        Profiles
            .select { Profiles.email eq email }
            .mapNotNull { resultRowToProfile(it) }
            .singleOrNull()
    }

    suspend fun addTokenToProfile(email: String, token: String) = dbQuery {
        Profiles.update({ Profiles.email eq email }) {
            it[Profiles.token] = token
        }
    }

    suspend fun registrationProfile(email: String, passwordHash: String, chatId: String?)
    = dbQuery {
        exposedLogger.info("Insert profile data from new user with email $email in DB")
        Profiles
            .insert {
                it[Profiles.email] = email
                it[password] = passwordHash
                it[Profiles.chatId] = chatId
            }
    }

    fun getUserIdByEmail(email: String): Int  {
        return Profiles
            .select { Profiles.email eq email }
            .firstNotNullOf { resultRowToProfile(it) }.id
    }

    suspend fun updatePassword(email: String, passwordHash: String) = dbQuery {
        exposedLogger.info("Update in DB password for user with email $email")
        Profiles.update({ Profiles.email eq email }) {
            it[password] = passwordHash
        }
    }

    suspend fun isChatIdExits(chatId: String): Boolean = dbQuery {
        !Profiles.select{Profiles.chatId eq chatId}.empty()
    }
}
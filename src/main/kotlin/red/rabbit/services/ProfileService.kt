package red.rabbit.services

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import red.rabbit.DatabaseFactory.dbQuery
import red.rabbit.models.Profile
import red.rabbit.models.Profiles

class ProfileService {

    private fun toProfileType(row: ResultRow): Profile =
        Profile(
            id = row[Profiles.id],
            email = row[Profiles.email],
            password = row[Profiles.password]
        )

    suspend fun getAllUsers(): List<Profile> = dbQuery {
        exposedLogger.info("Getting all users from DB")
        Profiles.selectAll().map { toProfileType(it) }
    }

    suspend fun getProfileByEmail(email: String): Profile? = dbQuery {
        exposedLogger.info("Get profile data from DB by email $email")
        Profiles.select {
            (Profiles.email eq email)
        }.mapNotNull { toProfileType(it) }
            .singleOrNull()
    }

    suspend fun registerProfile(email: String, passwordHash: String) = dbQuery {
        exposedLogger.info("Insert profile data from new user with email $email in DB")
        Profiles.insert {
            it[Profiles.email] = email
            it[password] = passwordHash
        }
    }

    suspend fun updatePassword(email: String, passwordHash: String) = dbQuery {
        exposedLogger.info("Update in DB password for user with email $email")
        Profiles.update({ Profiles.email eq email }) {
            it[password] = passwordHash
        }
    }
}
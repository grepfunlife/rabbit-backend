package red.rabbit.services

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import red.rabbit.DatabaseFactory.dbQuery
import red.rabbit.models.Profile
import red.rabbit.models.ProfileType

class ProfileService {
    suspend fun getAllUsers(): List<ProfileType> = dbQuery {
        Profile.selectAll().map { toProfileType(it) }
    }

    suspend fun getProfileByEmail(email: String): ProfileType? = dbQuery {
        Profile.select {
            (Profile.email eq email)
        }.mapNotNull { toProfileType(it) }
            .singleOrNull()
    }

    suspend fun registerProfile(email: String, passwordHash: String) = dbQuery {
        Profile.insert {
            it[Profile.email] = email
            it[password] = passwordHash
        }
    }

    private fun toProfileType(row: ResultRow): ProfileType =
        ProfileType(
            id = row[Profile.id],
            email = row[Profile.email],
            password = row[Profile.password]
        )
}
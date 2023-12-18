package red.rabbit.services

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import red.rabbit.DatabaseFactory.dbQuery
import red.rabbit.models.Profile
import red.rabbit.models.Profiles

class ProfileService {
    private fun resultRowToProfile(row: ResultRow): Profile =
        Profile(
            id = row[Profiles.id],
            email = row[Profiles.email],
            password = row[Profiles.password]
        )

    suspend fun getAllUsers(): List<Profile> = dbQuery {
        Profiles.selectAll().map { resultRowToProfile(it) }
    }

    suspend fun getProfileByEmail(email: String): Profile? = dbQuery {
        Profiles
            .select { Profiles.email eq email }
            .mapNotNull { resultRowToProfile(it) }
            .singleOrNull()
    }

    suspend fun registerProfile(email: String, passwordHash: String) = dbQuery {
        Profiles
            .insert {
                it[Profiles.email] = email
                it[password] = passwordHash
            }
    }
}
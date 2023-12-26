import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.junit.Test
import red.rabbit.models.habit.HabitRequest
import red.rabbit.models.habit.HabitResponse
import kotlin.test.assertEquals

class HabitRoutesTest : BaseTest() {
    @Test
    fun testSuccessfulAddHabit() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, "good", true))
        }
        assertEquals(OK, response.status)
    }

    @Test
    fun testUnsuccessfulNameIsAlreadyExistAddHabit() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val name = "bad"
        val response = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, name, false))
        }
        assertEquals(OK, response.status)

        val response1 = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, name, true))
        }
        assertEquals(BadRequest, response1.status)
        assertEquals("Reasons: Habit with name ${name} already exist", response1.body())
    }

    @Test
    fun testUnsuccessfulNameIsEmptyAddHabit() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, "", true))
        }
        assertEquals(BadRequest, response.status)
        assertEquals("Reasons: Name shouldn't be empty", response.body())
    }

    @Test
    fun testUnsuccessfulWithoutTokenAddHabit() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/habits/add") {
            contentType(Json)
            setBody(HabitRequest(1, "ok", true))
        }
        assertEquals(Unauthorized, response.status)
    }

    @Test
    fun testSuccessfulEditHabit() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseAdd = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(0, "no", true))
        }
        assertEquals(OK, responseAdd.status)

        val id = responseAdd.body<HabitResponse>().id!!

        val responseEdit = client.post("habits/edit") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(id, "yes", false))
        }
        assertEquals(OK, responseEdit.status)
    }

    @Test
    fun testUnsuccessfulHabitWithIdIsNotExistEditHabit() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("habits/edit") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(666, "yes", false))
        }
        assertEquals(BadRequest, response.status)
    }

    @Test
    fun testUnsuccessfulNameIsEmptyEditHabit() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseAdd = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(0, "habit", true))
        }
        assertEquals(OK, responseAdd.status)

        val id = responseAdd.body<HabitResponse>().id!!

        val responseEdit = client.post("habits/edit") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(id, "", false))
        }

        assertEquals(BadRequest, responseEdit.status)
    }

    @Test
    fun testSuccessfulDeleteHabit() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseAdd = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(0, "habit1", true))
        }
        assertEquals(OK, responseAdd.status)

        val id = responseAdd.body<HabitResponse>().id!!

        val responseDelete = client.post("habits/delete/") {
            header("Authorization", "Bearer $token")
            parameter("id", id)
        }

        assertEquals(OK, responseDelete.status)
    }

    @Test
    fun testUnsuccessfulHabitWithIdIsNotExistDeleteHabit() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("habits/delete/") {
            header("Authorization", "Bearer $token")
            parameter("id", 666)
        }
        assertEquals(BadRequest, response.status)
    }

    @Test
    fun testSuccessfulGetHabit() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val name = "habit2"
        val responseAdd = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(0, name, true))
        }
        assertEquals(OK, responseAdd.status)

        val id = responseAdd.body<HabitResponse>().id!!

        val responseGet = client.get("habits/") {
            header("Authorization", "Bearer $token")
            parameter("id", id)
        }

        assertEquals(OK, responseGet.status)
        assertEquals(name, responseGet.body<HabitResponse>().name)
    }

    @Test
    fun testUnsuccessfulHabitWithIdIsNotExistGetHabit() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val id = 666

        val response = client.get("habits/") {
            header("Authorization", "Bearer $token")
            parameter("id", id)
        }
        assertEquals(BadRequest, response.status)
        assertEquals("Habit with id = $id does not exist", response.body())
    }
}
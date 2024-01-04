import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.junit.Test
import red.rabbit.models.event.EventRequest
import red.rabbit.models.event.EventRequestBulk
import red.rabbit.models.event.EventResponse
import red.rabbit.models.habit.HabitRequest
import red.rabbit.models.habit.HabitResponse
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class EventRoutesTest : BaseTest() {

    val now = Clock.System.todayIn(timeZone = TimeZone.currentSystemDefault())

    @Test
    fun testSuccessfulAddEvent() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseHabit = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, "good", true))
        }
        assertEquals(OK, responseHabit.status)

        val habitId = responseHabit.body<HabitResponse>().id

        val response = client.post("events/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(EventRequest(1, now, habitId!!))
        }
        assertEquals(OK, response.status)
        assertNotNull(response.body<EventResponse>().id)
    }

    @Test
    fun testSuccessfulEditEvent() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseHabit = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, "bad", false))
        }
        assertEquals(OK, responseHabit.status)

        val habitId = responseHabit.body<HabitResponse>().id

        val responseAdd = client.post("events/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(EventRequest(1, now, habitId!!))
        }
        assertEquals(OK, responseAdd.status)

        val responseEdit = client.post("events/edit") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(EventRequest(1, now.plus(DatePeriod(days = 1)), habitId!!))
        }
        assertEquals(OK, responseEdit.status)
    }

    @Test
    fun testSuccessfulDeleteEvent() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseHabit = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, "habit", false))
        }
        assertEquals(OK, responseHabit.status)

        val habitId = responseHabit.body<HabitResponse>().id

        val responseAdd = client.post("events/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(EventRequest(1, now, habitId!!))
        }
        assertEquals(OK, responseAdd.status)
        val eventId = responseAdd.body<EventResponse>().id

        val responseEdit = client.post("events/delete") {
            header("Authorization", "Bearer $token")
            parameter("id", eventId)
        }
        assertEquals(OK, responseEdit.status)
    }

    @Test
    fun testUnsuccessfulEventWithIdIsNotExistDeleteEvent() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseEdit = client.post("events/delete") {
            header("Authorization", "Bearer $token")
            parameter("id", 90)
        }
        assertEquals(BadRequest, responseEdit.status)
    }

    @Test
    fun testSuccessfulGetEventByDate() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseHabit = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, "habit1", true))
        }
        assertEquals(OK, responseHabit.status)

        val habitId = responseHabit.body<HabitResponse>().id
        val date = now.plus(DatePeriod(days = 5))

        val responseAdd = client.post("events/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(EventRequest(1, date, habitId!!))
        }
        assertEquals(OK, responseAdd.status)
        val eventId = responseAdd.body<EventResponse>().id

        val response = client.get("events/") {
            header("Authorization", "Bearer $token")
            parameter("date", date)
        }
        assertEquals(OK, response.status)
        assertEquals(eventId, response.body<List<EventResponse>>().first().id)
    }

    @Test
    fun testSuccessfulAddBulkEvent() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseHabit = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, "habit2", true))
        }
        assertEquals(OK, responseHabit.status)

        val habitId = responseHabit.body<HabitResponse>().id

        val dates =
            listOf(now.minus(DatePeriod(days = 1)), now.minus(DatePeriod(days = 2)), now.minus(DatePeriod(days = 3)))

        val response = client.post("events/add/bulk") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(EventRequestBulk(dates, habitId!!))
        }
        assertEquals(OK, response.status)
        assertEquals(3, response.body<List<EventResponse>>().size)
    }
}
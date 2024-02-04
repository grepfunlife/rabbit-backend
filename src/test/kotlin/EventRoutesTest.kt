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
import kotlinx.serialization.json.Json
import org.junit.Ignore
import org.junit.Test
import red.rabbit.models.event.EventRequest
import red.rabbit.models.event.EventRequestBulk
import red.rabbit.models.event.EventResponse
import red.rabbit.models.habit.HabitRequest
import red.rabbit.models.habit.HabitResponse
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EventRoutesTest : BaseTest() {

    private val now = Clock.System.todayIn(timeZone = TimeZone.currentSystemDefault())

    @Test
    fun testSuccessfulAddEvent() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val date = now.plus(DatePeriod((0..4).random()))
        val habitName = "habitAddEvent"

        val responseHabit = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, habitName, true))
        }
        assertEquals(OK, responseHabit.status)

        val habitId = responseHabit.body<HabitResponse>().id

        val responseEvent = client.post("events/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(EventRequest(1, date, habitId!!))
        }
        assertEquals(OK, responseEvent.status)
        assertNotNull(responseEvent.body<EventResponse>().id)
    }

    @Test
    fun testSuccessfulEditEvent() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val date = now.plus(DatePeriod((1..4).random()))
        val habitName = "habitEditEvent"

        val responseHabit = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, habitName, false))
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
            setBody(EventRequest(1, date, habitId!!))
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

        val date = now.plus(DatePeriod((0..4).random()))
        val habitName = "habitDeleteEvent"

        val responseHabit = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, habitName, false))
        }
        assertEquals(OK, responseHabit.status)

        val habitId = responseHabit.body<HabitResponse>().id

        val responseAdd = client.post("events/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(EventRequest(1, date, habitId!!))
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
            parameter("id", (20..30).random())
        }
        assertEquals(BadRequest, responseEdit.status)
    }

    @Test
    @Ignore
    fun testSuccessfulGetEventByDate() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        val date = now.plus(DatePeriod((0..4).random()))
        val habitName = "habitNameGetByDate"

        val responseHabit = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, habitName, true))
        }
        assertEquals(OK, responseHabit.status)

        val habitId = responseHabit.body<HabitResponse>().id

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
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        val habitName = "bulkHabit1"

        val responseHabit = client.post("/habits/add") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(HabitRequest(1, habitName, true))
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
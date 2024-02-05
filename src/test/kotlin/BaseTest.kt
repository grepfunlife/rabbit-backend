import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.junit.Before
import red.rabbit.models.LoginRequest
import red.rabbit.models.RegistrationRequest
import red.rabbit.models.TokenResponse
import kotlin.test.assertEquals

open class BaseTest {
    var token = ""

    @Before
    fun getToken() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/auth/registration") {
            contentType(ContentType.Application.Json)
            setBody(RegistrationRequest("test12@mail.com", "test1234", "12345"))
        }

        val responseLogin = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("test12@mail.com", "test1234", "12345"))
        }
        assertEquals(OK, responseLogin.status)
        val responseGetToken = client.get("/auth/getTokenByChatId") {
            parameter("chatId", "12345")
        }
        assertEquals(OK, responseGetToken.status)
        token = responseGetToken.body<TokenResponse>().token!!
    }
}
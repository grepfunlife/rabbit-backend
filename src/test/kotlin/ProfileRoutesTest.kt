import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import red.rabbit.models.LoginRegister
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

const val INVALID_TOKEN_RESPONSE = "Token is not valid or has expired"

class ProfileRoutesTest {

    @Test
    fun testSuccessAuthorizationWithToken() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseRegister = client.post("/auth/register") {
            contentType(Json)
            setBody(LoginRegister("test13mail.com", "test"))
        }
        assertEquals(OK, responseRegister.status)

        val responseLogin = client.post("/auth/login") {
            contentType(Json)
            setBody(LoginRegister("test13mail.com", "test"))
        }
        assertEquals(OK, responseLogin.status)

        val token = responseLogin.body<LoginResponse>().token

        val response = client.get("/auth/test") {
            header(Authorization, "Bearer $token")
        }
        assertEquals(OK, response.status)
        assertEquals("Auth 2.1", response.bodyAsText())
    }

    @Test
    fun testUnsuccessfulAuthorizationWithoutToken() = testApplication {
        val response = client.get("/auth/test")
        assertEquals(Unauthorized, response.status)
        assertEquals(INVALID_TOKEN_RESPONSE, response.bodyAsText())
    }

    @Test
    fun testUnsuccessfulAuthorizationWithRandomToken() = testApplication {
        val randomValueToken = "dajkldjsld182903dklasakldjlaalskdasdsda"
        val response = client.get("/auth/test") {
            header(Authorization, "Bearer $randomValueToken")
        }
        assertEquals(Unauthorized, response.status)
        assertEquals(INVALID_TOKEN_RESPONSE, response.bodyAsText())
    }

    @Test
    fun testSuccessfulRegisterNewUser() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/auth/register") {
            contentType(Json)
            setBody(LoginRegister("test1mail.com", "test"))
        }
        assertEquals(OK, response.status)
    }

    @Test
    fun testSuccessfulLoginUser() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseRegister = client.post("/auth/register") {
            contentType(Json)
            setBody(LoginRegister("test12mail.com", "test"))
        }
        assertEquals(OK, responseRegister.status)

        val responseLogin = client.post("/auth/login") {
            contentType(Json)
            setBody(LoginRegister("test12mail.com", "test"))
        }
        assertEquals(OK, responseLogin.status)
        val token = responseLogin.body<LoginResponse>().token
        assertNotNull(token, "Token is not null")
    }
}


@Serializable
data class LoginResponse(val token: String)

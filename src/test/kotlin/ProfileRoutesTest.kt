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
import org.junit.Ignore
import red.rabbit.models.ChangePasswordRequest
import red.rabbit.models.LoginRequest
import red.rabbit.models.TokenResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ProfileRoutesTest {

    @Test
    fun testSuccessfulRegisterNewUser() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/auth/registration") {
            contentType(Json)
            setBody(LoginRequest("test1@mail.com", "testpass", null))
        }
        assertEquals(OK, response.status)
    }

    @Test
    @Ignore
    fun testSuccessfulLoginUser() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseRegister = client.post("/auth/registration") {
            contentType(Json)
            setBody(LoginRequest("test12@mail.com", "testpass", null))
        }
        assertEquals(OK, responseRegister.status)

        val responseLogin = client.post("/auth/login") {
            contentType(Json)
            setBody(LoginRequest("test12@mail.com", "testpass", null))
        }
        assertEquals(OK, responseLogin.status)
        val token = responseLogin.body<TokenResponse>().token
        assertNotNull(token, "Token is not null")
    }

    @Test
    fun testSuccessfulChangePassword() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseRegister = client.post("/auth/registration") {
            contentType(Json)
            setBody(LoginRequest("test13@mail.com", "testpass", "45656"))
        }
        assertEquals(OK, responseRegister.status)

        val responseLogin = client.post("/auth/login") {
            contentType(Json)
            setBody(LoginRequest("test13@mail.com", "testpass", "45656"))
        }
        assertEquals(OK, responseLogin.status)

        val responseGetToken = client.get("/auth/getTokenByChatId") {
            parameter("chatId", "45656")
        }
        assertEquals(OK, responseGetToken.status)
        val token = responseGetToken.body<TokenResponse>().token!!
        assertNotNull(token, "Token is not null")

        val responseChangePassword = client.post("/auth/changePassword") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(ChangePasswordRequest("test13@mail.com", "testpass", "superPassword"))
        }
        assertEquals(OK, responseChangePassword.status)
    }

    @Test
    fun testUnsuccessfulChangePasswordWithoutToken() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseRegister = client.post("/auth/registration") {
            contentType(Json)
            setBody(LoginRequest("test14@mail.com", "testpass", "56789"))
        }
        assertEquals(OK, responseRegister.status)

        val responseLogin = client.post("/auth/login") {
            contentType(Json)
            setBody(LoginRequest("test14@mail.com", "testpass", "56789"))
        }
        assertEquals(OK, responseLogin.status)

        val responseGetToken = client.get("/auth/getTokenByChatId") {
            parameter("chatId", "56789")
        }
        assertEquals(OK, responseGetToken.status)
        val token = responseGetToken.body<TokenResponse>().token!!
        assertNotNull(token, "Token is not null")

        val responseChangePassword = client.post("/auth/changePassword") {
            contentType(Json)
            setBody(ChangePasswordRequest("test14@mail.com", "testpass", "superPassword"))
        }
        assertEquals(Unauthorized, responseChangePassword.status)
    }

    @Test
    fun testUnsuccessfulChangePasswordIncorrectToken() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseRegister = client.post("/auth/registration") {
            contentType(Json)
            setBody(LoginRequest("test15@mail.com", "testpass", null))
        }
        assertEquals(OK, responseRegister.status)

        val responseLogin = client.post("/auth/login") {
            contentType(Json)
            setBody(LoginRequest("test15@mail.com", "testpass", null))
        }
        assertEquals(OK, responseLogin.status)

        val responseChangePassword = client.post("/auth/changePassword") {
            header("Authorization", "Bearer 1293lsadasdsahdjHJdskjadhskadhkjHjkds8123jkhkj@kjdsakd1")
            contentType(Json)
            setBody(ChangePasswordRequest("test15@mail.com", "testpass", "superPassword"))
        }
        assertEquals(BadRequest, responseChangePassword.status)
    }

    @Test
    fun testUnsuccessfulChangePasswordWithOldPassword() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseRegister = client.post("/auth/registration") {
            contentType(Json)
            setBody(LoginRequest("test16@mail.com", "testpass", "5678"))
        }
        assertEquals(OK, responseRegister.status)

        val responseLogin = client.post("/auth/login") {
            contentType(Json)
            setBody(LoginRequest("test16@mail.com", "testpass", "5678"))
        }
        assertEquals(OK, responseLogin.status)
        val responseGetToken = client.get("/auth/getTokenByChatId") {
            parameter("chatId", "5678")
        }
        assertEquals(OK, responseGetToken.status)
        val token = responseGetToken.body<TokenResponse>().token!!
        assertNotNull(token, "Token is not null")

        val responseChangePassword = client.post("/auth/changePassword") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(ChangePasswordRequest("test16@mail.com", "testpass", "testpass"))
        }
        assertEquals(BadRequest, responseChangePassword.status)
    }

    @Test
    fun testUnsuccessfulRegistrationWithEmptyPassword() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseRegister = client.post("/auth/registration") {
            contentType(Json)
            setBody(LoginRequest("test17@mail.com", "", null))
        }
        assertEquals(BadRequest, responseRegister.status)
    }
}

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
import red.rabbit.models.ChangePasswordRequest
import red.rabbit.models.CredentialsRequest
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
        val response = client.post("/auth/register") {
            contentType(Json)
            setBody(CredentialsRequest("test1mail.com", "test"))
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
            setBody(CredentialsRequest("test12@mail.com", "test"))
        }
        assertEquals(OK, responseRegister.status)

        val responseLogin = client.post("/auth/login") {
            contentType(Json)
            setBody(CredentialsRequest("test12@mail.com", "test"))
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

        val responseRegister = client.post("/auth/register") {
            contentType(Json)
            setBody(CredentialsRequest("test13@mail.com", "test"))
        }
        assertEquals(OK, responseRegister.status)

        val responseLogin = client.post("/auth/login") {
            contentType(Json)
            setBody(CredentialsRequest("test13@mail.com", "test"))
        }
        assertEquals(OK, responseLogin.status)
        val token = responseLogin.body<TokenResponse>().token
        assertNotNull(token, "Token is not null")

        val responseChangePassword = client.post("/auth/changePassword") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(ChangePasswordRequest("test13@mail.com", "test", "superPassword"))
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

        val responseRegister = client.post("/auth/register") {
            contentType(Json)
            setBody(CredentialsRequest("test14@mail.com", "test"))
        }
        assertEquals(OK, responseRegister.status)

        val responseLogin = client.post("/auth/login") {
            contentType(Json)
            setBody(CredentialsRequest("test14@mail.com", "test"))
        }
        assertEquals(OK, responseLogin.status)
        val token = responseLogin.body<TokenResponse>().token
        assertNotNull(token, "Token is not null")

        val responseChangePassword = client.post("/auth/changePassword") {
            contentType(Json)
            setBody(ChangePasswordRequest("test14@mail.com", "test", "superPassword"))
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

        val responseRegister = client.post("/auth/register") {
            contentType(Json)
            setBody(CredentialsRequest("test15@mail.com", "test"))
        }
        assertEquals(OK, responseRegister.status)

        val responseLogin = client.post("/auth/login") {
            contentType(Json)
            setBody(CredentialsRequest("test15@mail.com", "test"))
        }
        assertEquals(OK, responseLogin.status)

        val responseChangePassword = client.post("/auth/changePassword") {
            header("Authorization", "Bearer 1293lsadasdsahdjHJdskjadhskadhkjHjkds8123jkhkj@kjdsakd1")
            contentType(Json)
            setBody(ChangePasswordRequest("test15@mail.com", "test", "superPassword"))
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

        val responseRegister = client.post("/auth/register") {
            contentType(Json)
            setBody(CredentialsRequest("test16@mail.com", "test"))
        }
        assertEquals(OK, responseRegister.status)

        val responseLogin = client.post("/auth/login") {
            contentType(Json)
            setBody(CredentialsRequest("test16@mail.com", "test"))
        }
        val token = responseLogin.body<TokenResponse>().token
        assertEquals(OK, responseLogin.status)
        assertNotNull(token, "Token is not null")

        val responseChangePassword = client.post("/auth/changePassword") {
            header("Authorization", "Bearer $token")
            contentType(Json)
            setBody(ChangePasswordRequest("test16@mail.com", "test", "test"))
        }
        assertEquals(BadRequest, responseChangePassword.status)
    }

    @Test
    fun testUnsuccessfulRegistrationWithAnEmptyPassword() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseRegister = client.post("/auth/register") {
            contentType(Json)
            setBody(CredentialsRequest("test17@mail.com", ""))
        }
        assertEquals(BadRequest, responseRegister.status)
    }
}

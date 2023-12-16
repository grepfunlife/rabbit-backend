import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import java.nio.charset.Charset.defaultCharset
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testJwtAuthorization() = testApplication {
        val response = client.get("/auth/test")
        assertEquals(Unauthorized, response.status)
        assertEquals("Token is not valid or has expired", response.bodyAsText())
    }

    @Test
    fun testRegisterNewUser() = testApplication {
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
    fun testLoginUser() = testApplication {
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
        val resp = responseLogin.bodyAsText(defaultCharset())
        assertContains(resp, "token")
    }
}

@Serializable
data class LoginRegister(val email: String, val password: String)

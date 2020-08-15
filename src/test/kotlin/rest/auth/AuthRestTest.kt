package rest.auth

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import rest.AbstractRestTest
import server.domain.auth.JWTToken
import server.domain.auth.LoginForm
import server.domain.auth.RegisterForm
import server.domain.auth.getLogInForm

fun AbstractRestTest.withLoggedIn(block: (jwtToken: JWTToken) -> Unit) {
    val result = userAuthClient.login(
        getLogInForm("alice", "alice")
    )

    block.invoke(
        JWTToken(
            result["access_token"] as String,
            result["refresh_token"] as String
        )
    )
}


@QuarkusTest
class AuthRestTest : AbstractRestTest() {

    private fun sendLogout(accessToken: String, refreshToken: String): String {
        val response = sendPost("/auth/logout", bearerToken = accessToken, body = refreshToken)
        return response.body.asString()
    }

    @Test
    fun profileWithoutToken() {
        sendGet("/auth/profile").then().statusCode(401)
    }

    @Test
    fun loginTest() {
        val loginForm = LoginForm(
            "alice", "alice"
        )

        val body = toJson(loginForm)

        var response = sendPost(path = "/auth/login", body = body)
        val jwt = response.body.`as`<JWTToken>(JWTToken::class.java) as JWTToken

        val accessToken = jwt.accessToken
        response = sendGet(path = "/auth/profile", bearerToken = accessToken)
        val responseBody = response.body.asString()
        Assertions.assertEquals("Hello alice", responseBody)
    }

    @Test
    fun logoutWithoutTokenTest() {
        val body = sendLogout("test", "test")
        Assertions.assertEquals(0, body.length)
    }

    @Test
    fun logoutWithRightTokenTest() = withLoggedIn { token ->
        val body = sendLogout(token.accessToken, token.refreshToken)
        Assertions.assertEquals(0, body.length)
    }


    @Test
    fun logoutWithWrongRefreshTokenTest() = withLoggedIn { jwtToken ->
        val body = sendLogout(jwtToken.accessToken, "xD")
        Assertions.assertNotEquals(0, body.length)
    }

    @Test
    fun logoutWithWrongAccessTokenButRightRefreshToken() = withLoggedIn { jwtToken ->
        val body = sendLogout("xD", jwtToken.refreshToken)
        Assertions.assertEquals(0, body.length)
    }

    @Test
    fun registerTest() {
        val registerForm = RegisterForm(
            "oberstrike2",
            "mewtu123",
            "oberstrike@gmx.de"
        )
        val response = sendPost(path = "/auth/register", body =  toJson(registerForm))
        response.then().statusCode(200)

    }
}
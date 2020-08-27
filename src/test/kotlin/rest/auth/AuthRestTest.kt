package rest.auth

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import rest.AbstractRestTest
import server.domain.auth.*
import util.createRegisterForm
import javax.ws.rs.core.Form


@QuarkusTest
@Testcontainers
class AuthRestTest : AbstractRestTest() {

    private fun sendLogout(accessToken: String, refreshToken: String): String {
        val response = sendPost("/auth/logout", bearerToken = accessToken, body = refreshToken)
        return response.body.asString()
    }

    @Test
    fun resetPasswordFailWrongEmailTest() = withRegistered { _, jwtToken ->
        val resetPasswordPath = "/auth/resetPassword"

        //Wrong email
        val passwordResetForm = PasswordResetForm(
            "alice@gmx.de",
            "mewtu123",
            "mewtu123"
        )

        val response = sendPost(resetPasswordPath, toJson(passwordResetForm), bearerToken = jwtToken.accessToken)
        Assertions.assertNotEquals(200, response.statusCode)

    }

    @Test
    fun resetPasswordFailWrongPasswordsTest() = withRegistered { _, jwtToken ->
        val resetPasswordPath = "/auth/resetPassword"

        //Wrong email
        val passwordResetForm = PasswordResetForm(
            null,
            "mewtu1234",
            "mewtu123"
        )

        val response = sendPost(resetPasswordPath, toJson(passwordResetForm), bearerToken = jwtToken.accessToken)
        Assertions.assertNotEquals(200, response.statusCode)
    }


    @Test
    fun resetPasswordTest() = withRegistered { user, jwtToken ->
        val resetPasswordPath = "/auth/resetPassword"

        val passwordResetForm = PasswordResetForm(
            null,
            "mewtu123",
            "mewtu123"
        )

        //Test if new Password doesnt fit
        sendPost(
            path = "/auth/login", body = LoginForm(
                username = user.username,
                password = passwordResetForm.password
            )
        ).then().statusCode(401)



        sendPost(resetPasswordPath, toJson(passwordResetForm), bearerToken = jwtToken.accessToken)

        //Test if new Password fits
        sendPost(
            path = "/auth/login", body = LoginForm(
                username = user.username,
                password = passwordResetForm.password
            )
        ).then().statusCode(200)


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
        val registerForm = createRegisterForm()
        val response = sendPost(path = "/auth/register", body = toJson(registerForm))
        response.then().statusCode(204)

        keyCloakService.delete(registerForm.username)
    }

    @Test
    fun registerFailTest(){
        val registerForm = createRegisterForm()
        registerForm.passwordConfirm = ""
        val response = sendPost(path = "/auth/register", body = toJson(registerForm))
        response.then().statusCode(400)
    }
}

fun AbstractRestTest.withLoggedIn(
    loginForm: Form = getLogInForm("alice", "alice"),
    block: (jwtToken: JWTToken) -> Unit
) {
    val result = userAuthClient.login(
        loginForm
    )

    block.invoke(
        JWTToken(
            result["access_token"] as String,
            result["refresh_token"] as String
        )
    )
}

fun AbstractRestTest.withRegistered(block: (user: UserDTO, jwtToken: JWTToken) -> Unit) {
    val user = createRegisterForm()
    keyCloakService.register(user.username, user.password, user.email)

    withLoggedIn(getLogInForm(username = user.username, password = user.password)) {
        block.invoke(
            UserDTO(
                username = user.username,
                email = user.email
            ), it
        )
    }

    keyCloakService.delete(username = user.username)

}

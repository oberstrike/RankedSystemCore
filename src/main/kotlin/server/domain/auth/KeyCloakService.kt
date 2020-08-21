package server.domain.auth

import io.quarkus.runtime.annotations.RegisterForReflection
import java.math.BigDecimal
import javax.ws.rs.core.Form

interface KeyCloakService {
    fun delete(username: String)
    fun register(username: String, password: String, email: String)
    fun getUserIdByUsername(username: String): UserDTO?
    fun resetPassword(email: String, newPassword: String)
}


@RegisterForReflection
data class PasswordResetForm(
    var email: String? = null,
    var password: String = "",
    var passwordConfirm: String = ""
)

@RegisterForReflection
data class LoginForm(
    var username: String = "",
    var password: String = ""
)

@RegisterForReflection
data class RegisterForm(
    var username: String = "",
    var password: String = "",
    var passwordConfirm: String = "",
    var email: String = ""
)

@RegisterForReflection
data class UserDTO(
    var id: String = "",
    var username: String = "",
    var email: String? = ""
)

interface IForm {
    fun isValid(): Boolean
}


fun getLogInForm(username: String, password: String): Form {
    return Form()
        .param("grant_type", "password")
        .param("username", username)
        .param("password", password)
        .param("scope", "profile")
        .param("client_id", "backend-service")
        .param("client_secret", "secret")
}

fun getLogoutForm(refreshToken: String): Form {
    return Form()
        .param("client_id", "backend-service")
        .param("client_secret", "secret")
        .param("refresh_token", refreshToken)
        .param("scope", "profile")
}


fun createTokenByMap(result: Map<String, Any>): JWTToken {
    val accessToken = result["access_token"] as String
    val refreshToken = result["refresh_token"] as String
    val refreshExpiresIn = result["refresh_expires_in"] as BigDecimal
    val scope = result["scope"] as String
    val tokenType = result["token_type"] as String
    val sessionState = result["session_state"] as String
    val expiresIn = result["expires_in"] as BigDecimal

    return JWTToken(
        accessToken, refreshToken, refreshExpiresIn, scope, tokenType, sessionState, expiresIn
    )
}

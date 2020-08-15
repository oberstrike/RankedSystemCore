package server.domain.auth

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.keycloak.admin.client.KeycloakBuilder
import java.math.BigDecimal
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.Form
import javax.ws.rs.core.MediaType


@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tags(
    Tag(name = "Auth", description = "The path to manage the status of the user")
)
class AuthResource {

    @Inject
    @RestClient
    lateinit var userAuthClient: UserAuthClient

    @Inject
    lateinit var securityIdentity: SecurityIdentity

    @Inject
    lateinit var jwt: JsonWebToken

    @Inject
    lateinit var keyCloakService: KeyCloakService


    @Path("/profile")
    @GET
    @Authenticated
    fun profile(): String {
        return "Hello ${securityIdentity.principal.name}"
    }


    @Path("/login")
    @POST
    fun login(@RequestBody(name = "loginForm") loginForm: LoginForm): JWTToken {
        return createTokenByMap(userAuthClient.login(getLogInForm("alice", "alice")))
    }


    @Path("/logout")
    @POST
    @Authenticated
    @SecurityRequirements(
        value = [
            SecurityRequirement(name = "bearerAuth")
        ]
    )
    fun logout(@RequestBody(name = "refreshToken") refreshToken: String): Map<String, Any> {
        return userAuthClient.logout("bearer ${jwt.rawToken}", getLogoutForm(refreshToken))
    }


    @Path("/register")
    @POST
    fun register(@RequestBody(name = "registerForm") registerForm: RegisterForm) {
        println("Start")
        keyCloakService.register("oberstikr2", "mewtu123", "oberstrike@gmx.de")
        println("End")

    }


}

data class JWTToken(
    val accessToken: String = "",
    val refreshToken: String = "",
    val refreshExpiresIn: BigDecimal = BigDecimal.ZERO,
    val scope: String = "",
    val tokenType: String = "",
    val sessionState: String = "",
    val expiresIn: BigDecimal = BigDecimal.ZERO
)

data class LoginForm(
    val username: String = "",
    val password: String = ""
)

data class RegisterForm(
    val username: String = "",
    val password: String = "",
    val email: String = ""
)

@RegisterRestClient(baseUri = "http://localhost:8180/auth/realms/quarkus/protocol/openid-connect")
interface UserAuthClient {

    @Path("/token")
    @POST
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun login(@RequestBody login: Form): Map<String, Any>


    @Path("/logout")
    @POST
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun logout(@HeaderParam("Authorization") authHeaderValue: String, @RequestBody logout: Form): Map<String, Any>

}

@RegisterRestClient(baseUri = "")
interface AdminAuthClient {

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
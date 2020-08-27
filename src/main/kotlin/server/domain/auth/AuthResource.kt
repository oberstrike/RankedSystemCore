package server.domain.auth

import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal
import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags
import org.eclipse.microprofile.rest.client.inject.RestClient

import java.math.BigDecimal
import javax.inject.Inject

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import kotlin.Exception


@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tags(
    Tag(name = "Auth", description = "The path to manage the status of the user")
)
//@RegisterProvider(LoggingFilter::class)
class AuthResource {

    @Inject
    @RestClient
    lateinit var userAuthClient: UserAuthClient

    @Inject
    lateinit var securityIdentity: SecurityIdentity

    @Inject
    lateinit var jwt: JsonWebToken

    @Inject
    lateinit var keyCloakService: KeyCloakServiceImpl


    @GET
    fun getKeycloak(): String {
        return keyCloakService.loginUrl()
    }

    @Path("/profile")
    @GET
    @Authenticated
    fun profile(): String {
        return "Hello ${securityIdentity.principal.name}"
    }


    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun login(loginForm: LoginForm): JWTToken {
        return createTokenByMap(userAuthClient.login(getLogInForm(loginForm.username, loginForm.password)))
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
    fun register(registerForm: RegisterForm) {
        if (!registerForm.isValid())
            throw RegistrationException("Error in registration")

        val userDTO = keyCloakService.register(registerForm.username, registerForm.password, registerForm.email)


    }

    @Path("/username/{username}")
    @GET
    @Throws(UserIdNotFoundException::class)
    fun getUserByUserId(@PathParam("username") username: String?): UserDTO? {
        if (username == null)
            throw UserIdNotFoundException()
        return keyCloakService.getUserIdByUsername(username)
    }


    @Path("/resetPassword")
    @POST
    fun resetPassword(resetPassword: PasswordResetForm?) {
        val myEmail = (securityIdentity.principal as OidcJwtCallerPrincipal).claims.getClaimValue("email") as String?

        if (resetPassword == null)
            throw PasswordEmptyException()
        if (resetPassword.password != resetPassword.passwordConfirm)
            throw PasswordsNotEqualException()
        if (resetPassword.email == null)
            resetPassword.email = myEmail
        if (resetPassword.email == null)
            throw EmailNotFoundException()

        if (resetPassword.email != myEmail)
            if (!securityIdentity.hasRole("ADMIN"))
                throw NoPermissionException("No Permission to change the password")



        keyCloakService.resetPassword(resetPassword.email!!, resetPassword.password)

    }

}

class RegistrationException(msg: String) : Exception(msg)
class NoPermissionException(msg: String) : BadRequestException(msg)
class EmailNotFoundException(msg: String = "") : Exception(msg)
class PasswordEmptyException(msg: String = "") : Exception(msg)
class PasswordsNotEqualException(msg: String = "") : Exception(msg)
class UserIdNotFoundException(msg: String = "") : Exception(msg)

data class JWTToken(
    // val rawToken: String = "",
    val accessToken: String = "",
    val refreshToken: String = "",
    val refreshExpiresIn: BigDecimal = BigDecimal.ZERO,
    val scope: String = "",
    val tokenType: String = "",
    val sessionState: String = "",
    val expiresIn: BigDecimal = BigDecimal.ZERO
)

fun RegisterForm.isValid(): Boolean {
    if (password != passwordConfirm)
        return false

    /*
    if (username.length < 8)
        return false
    var regex = Regex("[a-c]")
    if (!regex.containsMatchIn(password))
        return false
    regex = Regex(("[A-C]"))
    if (!regex.containsMatchIn(password))
        return false
    regex = Regex("\\d")
    if (!regex.containsMatchIn(password))
        return false
*/
    return true
}

package domain.auth

import domain.AbstractDomainTest
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.h2.H2DatabaseTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import server.domain.auth.RegisterForm
import server.domain.auth.UserDTO
import server.domain.auth.getLogInForm
import util.createRegisterForm
import java.nio.file.attribute.UserPrincipalNotFoundException

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource::class)
class KeyCloakServiceTest : AbstractDomainTest() {

    @Test
    fun getByUsername() {
        val username = "alice"
        val result = keyCloakService.getUserIdByUsername(username)
        Assertions.assertNotNull(result)
    }

    @Test
    fun deleteTest() = withUser("oberstriker") {
        keyCloakService.delete("oberstriker")
        val test = keyCloakService.getUserIdByUsername("oberstriker")
        Assertions.assertNull(test)
    }

    @Test
    fun resetPasswordWithRightPassword() = withUser("oberstriker") {

        keyCloakService.resetPassword(it.email!!, "newPassword")
        val map = authClient.login(getLogInForm("oberstriker", "newPassword"))
        Assertions.assertEquals(8, map.size)
    }

    @Test
    fun resetPasswordWithWrongPassword() = withUser("oberstriker") {
        keyCloakService.resetPassword(it.email!!, "newPassword")
        Assertions.assertThrows(Exception::class.java) {
            authClient.login(getLogInForm("oberstriker", "password"))
        }

    }


    @Test
    fun registerTest() {
        val registerForm = createRegisterForm()

        keyCloakService.register(registerForm.username, registerForm.password, registerForm.email)
        val result = keyCloakService.getUserIdByUsername(registerForm.username)
        Assertions.assertNotNull(result)
        keyCloakService.delete(registerForm.username)
    }
}

fun AbstractDomainTest.withUser(username: String, block: (UserDTO) -> Unit) {
    keyCloakService.register(username, "password", "email@gmx.de")
    val user = keyCloakService.getUserIdByUsername(username)
    block.invoke(user!!)
    try {
        keyCloakService.delete(username)
    } catch (exception: UserPrincipalNotFoundException) {
        println("User was already deleted")
    }

}
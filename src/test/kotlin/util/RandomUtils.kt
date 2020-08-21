package util

import net.andreinc.mockneat.unit.types.Chars.chars
import net.andreinc.mockneat.unit.user.Emails.emails
import net.andreinc.mockneat.unit.user.Passwords.passwords
import net.andreinc.mockneat.unit.user.Users.users

import server.domain.auth.LoginForm
import server.domain.auth.RegisterForm
import server.domain.auth.UserDTO

fun createLoginForm(): LoginForm = LoginForm(
    username = users().get(),
    password = passwords().strong().get()
)

fun createRegisterForm(): RegisterForm {
    val password = passwords().medium().get()


    return RegisterForm(
        username = users().get(),
        password = password,
        passwordConfirm = password,
        email = emails().domain("gmx").get()
    )

}

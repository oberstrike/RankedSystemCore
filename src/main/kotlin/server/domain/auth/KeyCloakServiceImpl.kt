package server.domain.auth

import io.quarkus.security.User
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.hibernate.annotations.NotFound
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import java.nio.file.attribute.UserPrincipalNotFoundException

import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class KeyCloakServiceImpl(
    @ConfigProperty(name = "service.serverUrl") val serverUrl: String,
    @ConfigProperty(name = "quarkus.oidc.client-id")
    val clientId: String,
    @ConfigProperty(name = "quarkus.oidc.credentials.secret") val secret: String
) : KeyCloakService {

    private var keycloak = KeycloakBuilder.builder()
        .serverUrl(serverUrl)
        .realm("master")
        .clientId("admin-cli")
        .clientSecret(secret)
        .username("admin")
        .password("admin")
        .build()


    override fun delete(username: String) {
        val realmResource = keycloak.realm("quarkus")
        val usersResource = realmResource.users()
        val user = usersResource.list().firstOrNull { it.username == username } ?: throw UserPrincipalNotFoundException(
            username
        )

        val id = user.id

        usersResource.delete(id)
    }


    override fun register(username: String, password: String, email: String) {
        val realmResource = keycloak.realm("quarkus")
        val usersResource = realmResource.users()

        val userRepresentation = UserRepresentation()
        userRepresentation.username = username
        userRepresentation.email = email
        userRepresentation.isEmailVerified = false
        userRepresentation.isEnabled = true

        val credentialRepresentation = CredentialRepresentation()
        credentialRepresentation.isTemporary = false
        credentialRepresentation.type = CredentialRepresentation.PASSWORD
        credentialRepresentation.value = password
        userRepresentation.credentials = listOf(credentialRepresentation)

        val response = usersResource.create(userRepresentation)

        when (response.status) {
            201 -> {
                val userId = response.location.path.split("/").last()
                //TODO Add role to user

            }
            409 -> {
            }
            else -> {

            }
        }
    }

    override fun getUserIdByUsername(username: String): UserDTO? {
        val realmResource = keycloak.realm("quarkus")
        val usersResource = realmResource.users()

        return usersResource.search(username).map { UserDTO(it.id, it.username, it.email) }.firstOrNull()
    }

    override fun resetPassword(email: String, newPassword: String) {
        val userId = keycloak.realm("quarkus")
            .users()
            .search(email, null, null)
            .stream()
            .filter { email == it.email }
            .map(UserRepresentation::getId)
            .findFirst()
            .orElse(null)
            ?: throw UserPrincipalNotFoundException("There is no user with the email $email")

        val credentialRepresentation = CredentialRepresentation()
        credentialRepresentation.isTemporary = false
        credentialRepresentation.type = CredentialRepresentation.PASSWORD
        credentialRepresentation.value = newPassword
        keycloak.realm("quarkus")
            .users()
            .get(userId)
            .resetPassword(credentialRepresentation)


    }

}


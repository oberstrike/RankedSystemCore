package server.domain.auth

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import java.nio.file.attribute.UserPrincipalNotFoundException
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class KeyCloakServiceImpl(
    @ConfigProperty(name = "service.admin.serverUrl") private val serverUrl: String,
    @ConfigProperty(name = "service.admin.client-id") private val clientId: String,
    @ConfigProperty(name = "service.admin.secret") private val secret: String,
    @ConfigProperty(name = "service.admin.username") private val username: String,
    @ConfigProperty(name = "service.admin.password") private val password: String,
    @ConfigProperty(name = "service.admin.loginUrl") private val loginUrl: String

) : KeyCloakService {

    private var keycloak = KeycloakBuilder.builder()
        .serverUrl(serverUrl)
        .realm("master")
        .clientId(clientId)
        .clientSecret(secret)
        .username(username)
        .password(password)
        .build()

    override fun loginUrl() = loginUrl

    override fun hasRoleByUserId(role: String, id: String): Boolean {
        val userResource = keycloak.realm("quarkus")
            .users()
        return userResource.get(id).roles().realmLevel().listAll().map { it.name }.contains(role)
    }

    override fun addRoleToUserById(role: String, id: String) {
        val frontendUserRole = keycloak.realm("quarkus")
            .roles()
            .list()
            .stream()
            .filter { roleRepresentation: RoleRepresentation -> role == roleRepresentation.name }
            .findFirst().get()


        keycloak.realm("quarkus")
            .users()
            .get(id)
            .roles()
            .realmLevel()
            .add(listOf(frontendUserRole))

    }


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
                addRoleToUserById("user", userId)
            }
            else -> {
                throw RegistrationException("Oops there was an error in the registration")
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


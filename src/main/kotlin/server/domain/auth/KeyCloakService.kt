package server.domain.auth

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation

import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class KeyCloakService(
    @ConfigProperty(name = "service.serverUrl")
    final val serverUrl: String,
    @ConfigProperty(name = "quarkus.oidc.client-id")
    val clientId: String,
    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    final val secret: String
) {

    private var keycloak = KeycloakBuilder.builder()
        .serverUrl(serverUrl)
        .realm("master")
        .clientId("admin-cli")
        .clientSecret(secret)
        .username("admin")
        .password("admin")
        .build()

    fun register(username: String, password: String, email: String) {
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


}

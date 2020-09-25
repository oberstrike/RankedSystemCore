package util

import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import dasniko.testcontainers.keycloak.KeycloakContainer
import org.testcontainers.containers.GenericContainer
import server.domain.auth.UserAuthClient


class KeycloakContainerCreator : IContainerCreator<KeycloakContainer> {

    companion object {
        const val AUTH_SERVER_HOST = "localhost"
        const val ADMIN_USERNAME = "admin"
        const val ADMIN_PASSWORD = "admin"
        const val REALM_IMPORT_FILE = "/imports/realm-export.json"
        const val PORT = 8181
        const val IMAGE_NAME = "quay.io/keycloak/keycloak:latest"
        const val REALM_NAME = "quarkus"
    }

    override fun getConfig(): MutableMap<String, String> {
        return mutableMapOf(
            "quarkus.oidc.auth-server-url" to "http://$AUTH_SERVER_HOST:$PORT/auth/realms/$REALM_NAME",
            "${UserAuthClient::class.java.`package`.name}.${UserAuthClient::class.java.simpleName}/mp-rest/url"
                    to "http://$AUTH_SERVER_HOST:$PORT/auth/realms/$REALM_NAME/protocol/openid-connect",
            "service.admin.serverUrl" to "http://${AUTH_SERVER_HOST}:${PORT}/auth",
            "service.admin.loginUrl" to "http://${AUTH_SERVER_HOST}:${PORT}/auth/realms/$REALM_NAME}/account"
        )
    }

    override fun getContainer(): GenericContainer<KeycloakContainer> {
        return KeycloakContainer(IMAGE_NAME)
            .withAdminUsername(ADMIN_USERNAME)
            .withAdminPassword(ADMIN_PASSWORD)
            .withRealmImportFile(REALM_IMPORT_FILE)
            .withExposedPorts(PORT)
            .withCreateContainerCmdModifier { cmd ->
                cmd.withHostConfig(HostConfig.newHostConfig().withPortBindings(PortBinding.parse("$PORT:8080")))
            }

    }

}
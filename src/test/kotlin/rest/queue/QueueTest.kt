package rest.queue

import io.quarkus.test.common.http.TestHTTPResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import rest.AbstractRestTest
import rest.auth.withLoggedIn
import server.domain.auth.JWTToken
import java.io.IOException
import java.net.URI
import java.util.concurrent.LinkedBlockingDeque
import javax.websocket.*


@QuarkusTest
class QueueTest : AbstractRestTest() {

    companion object {
        private val MESSAGES = LinkedBlockingDeque<String>()
    }


    @TestHTTPResource("/chat")
    lateinit var uri: URI


    @Test
    fun testWebsocketNotLoggedIn() {
        val endpoint = Client()
        val endpointConfigBuilder = ClientEndpointConfig.Builder.create()
        endpointConfigBuilder.configurator(Client.Configurator("bearer Random"))
        val endpointConfig = endpointConfigBuilder.build()


        assertThrows<IOException> {
            ContainerProvider.getWebSocketContainer().connectToServer(endpoint, endpointConfig, uri).use { session ->
                session.asyncRemote.sendObject("Hallo Welt")
            }
        }

    }


    @Test
    fun testWebsocketLoggedIn() = withLoggedIn { token: JWTToken ->
        val endpoint = Client()
        val endpointConfigBuilder = ClientEndpointConfig.Builder.create()
        endpointConfigBuilder.configurator(Client.Configurator("bearer ${token.accessToken}"))
        val endpointConfig = endpointConfigBuilder.build()

        ContainerProvider.getWebSocketContainer().connectToServer(endpoint, endpointConfig, uri).use { session ->
            session.asyncRemote.sendObject("Hallo Welt")
        }

    }

    @ClientEndpoint
    class Client : Endpoint() {

        class Configurator(private val token: String) : ClientEndpointConfig.Configurator() {
            override fun beforeRequest(headers: MutableMap<String, MutableList<String>>) {
                headers["Authorization"] = mutableListOf(token)
                super.beforeRequest(headers)
            }
        }

        @OnMessage
        fun message(msg: String) {
            MESSAGES.add(msg)
        }

        @OnOpen
        override fun onOpen(session: Session, p1: EndpointConfig?) {
            MESSAGES.add("CONNECT")
            // Send a message to indicate that we are ready,
            // as the message handler may not be registered immediately after this callback.
            session.asyncRemote.sendText("Ready")
        }
    }
}
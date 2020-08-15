package domain.queue

import io.quarkus.test.common.http.TestHTTPResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Test
import java.net.URI
import java.util.concurrent.LinkedBlockingDeque
import javax.websocket.*


@QuarkusTest
class WebsocketTest {

    companion object {
        val MESSAGES = LinkedBlockingDeque<String>()
    }


    @TestHTTPResource("/queue/1o1")
    lateinit var uri: URI

    @Test
    fun testWebsocketChat() {
        val session = ContainerProvider.getWebSocketContainer().connectToServer(Client::class.java, uri)

        session.asyncRemote.sendText("hello world");

    }


}


@ClientEndpoint
class Client {
    @OnOpen
    fun open(session: Session) {
        WebsocketTest.MESSAGES.add("CONNECT")
        // Send a message to indicate that we are ready,
        // as the message handler may not be registered immediately after this callback.
        session.asyncRemote.sendText("_ready_")
    }

    @OnMessage
    fun message(msg: String) {
        WebsocketTest.MESSAGES.add(msg)
    }
}
package server.domain.queue

import com.sun.security.auth.UserPrincipal
import io.quarkus.undertow.websockets.runtime.devmode.HotReplacementWebsocketEndpoint
import org.eclipse.microprofile.rest.client.inject.RestClient
import server.domain.auth.UserAuthClient
import java.lang.RuntimeException
import java.security.Principal
import java.util.concurrent.ConcurrentHashMap
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.json.bind.JsonbBuilder
import javax.servlet.*
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse
import javax.websocket.*
import javax.websocket.server.HandshakeRequest
import javax.websocket.server.ServerEndpoint
import javax.websocket.server.ServerEndpointConfig


@WebFilter("/chat")
@ApplicationScoped
class ExampleFilter : Filter {

    @RestClient
    lateinit var authClient: UserAuthClient


    override fun init(filterConfig: FilterConfig?) {
        super.init(filterConfig)
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        val authHeader = httpRequest.getHeader("Authorization")

        val wrappedRequest = object : HttpServletRequestWrapper(httpRequest) {
            override fun getUserPrincipal(): Principal? {
                return try {
                    val userMap = authClient.userInfo(authHeader)
                    val username = userMap?.get("preferred_username") as String
                    UserPrincipal(username)
                } catch (exception: Exception) {
                    null
                }
            }
        }
        val principal = wrappedRequest.userPrincipal
        if (principal == null) httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
        else chain.doFilter(wrappedRequest, response)


    }
}

@ServerEndpoint(
    value = "/chat",
    configurator = GameQueueWebsocketServer.ServerConfigurator::class
)
@ApplicationScoped
class GameQueueWebsocketServer : Endpoint() {

    var sessions: MutableMap<String, Session> = ConcurrentHashMap()


    @OnOpen
    override fun onOpen(session: Session, p1: EndpointConfig?) {
        val serverEndpointConfig = p1 as ServerEndpointConfig
        val configurator = p1.configurator as ServerConfigurator

        //   configurator.authClient = authClient
        sessions[session.id] = session
    }

    @OnClose
    fun onClose(session: Session) {
        sessions.remove(session.id)
        broadcast("User ${session.id} left")
    }

    @OnError
    override fun onError(session: Session, throwable: Throwable) {
        sessions.remove(session.id)

        broadcast("User ${session.id} left on error: $throwable")
    }

    @OnMessage
    fun onMessage(content: String) {
        println(content)
    }

    private fun broadcast(content: String) {
        for (sessionPair in sessions) {
            val session = sessionPair.value
            session.asyncRemote.sendText(content)
        }
    }


    @ApplicationScoped
    class ServerConfigurator(
    ) : ServerEndpointConfig.Configurator() {

        override fun modifyHandshake(
            sec: ServerEndpointConfig?,
            request: HandshakeRequest?,
            response: HandshakeResponse?
        ) {

            val headers = request?.headers ?: throw RuntimeException("No Headers")
            val auth = request.userPrincipal
            if (!request.headers.containsKey("Authorization")) throw RuntimeException("No Header Authorization")
            super.modifyHandshake(sec, request, response)

        }
    }

}

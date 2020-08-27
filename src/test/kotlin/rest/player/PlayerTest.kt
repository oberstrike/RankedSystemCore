package rest.player

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Test
import rest.AbstractRestTest
import rest.auth.withRegistered
import server.domain.ranked.RankedPlayerDTO

@QuarkusTest
class PlayerTest : AbstractRestTest() {

    private val addPlayerRoute = "/player"

    @Test
    fun addPlayerWithoutAuthTest() {
        val player = RankedPlayerDTO(
            name = "player"
        )
        sendPost(addPlayerRoute, body = toJson(player)).then().statusCode(401)
    }


    @Test
    fun addPlayerWithAuthTest() = withRegistered { user, jwtToken ->
        val player = RankedPlayerDTO(
            name = user.username
        )
        val response = sendPost(path = addPlayerRoute, body = toJson(player), bearerToken = jwtToken.accessToken)
        assert(response.statusCode == 200)
        println(response.body.asString())




    }
}
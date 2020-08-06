package domain.match

import domain.AbstractDomainTest
import elo.MatchResultType
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.h2.H2DatabaseTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import server.domain.match.MatchDTO
import java.lang.Exception
import javax.transaction.Transactional

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource::class)
class MatchServiceTest : AbstractDomainTest() {

    @Test
    @Transactional
    fun createNewTest() = withPlayer { player ->
        val playerId = player.id

        val matchDTO = MatchDTO()
        matchDTO.teamA = arrayOf(playerId)

        val match = matchService.createFromDTO(matchDTO)
        Assertions.assertNotNull(match)
        match!!

        val all = matchService.findAll()
        assert(all.isNotEmpty())

        Assertions.assertNotEquals(0, match.teamA.size)
        println(match)

        //Cleanup
        match.delete()
    }

    @Test
    fun createFromDTOTest() = withMatch { match ->
        Assertions.assertNotNull(match)

        val matchDTO = matchService.convertToDTO(match)
        Assertions.assertNotNull(matchDTO)

        Assertions.assertEquals(match.id, matchDTO.id)
        Assertions.assertEquals(match.finished, matchDTO.finished)
    }

    @Test
    fun convertToDTOTest() = withMatch { match ->
        val matchDTO = matchService.convertToDTO(match)
        Assertions.assertEquals(match.id, matchDTO.id)
        Assertions.assertEquals(match.finished, matchDTO.finished)
        Assertions.assertEquals(match.teamA.map { it.id }.toTypedArray().size, matchDTO.teamA.size)
        Assertions.assertEquals(match.teamB.map { it.id }.toTypedArray().size, matchDTO.teamB.size)
    }

    @Test
    fun findAllTest() = withMatch {
        val all = matchService.findAll()
        Assertions.assertEquals(1, all.size)
    }

    @Test
    fun finishGameTest() = withMatch {
        val resultType = MatchResultType.TEAM_A_WINS

        val result = matchService.finishGame(resultType, matchService.convertToDTO(it))
        Assertions.assertNotNull(result)
        Assertions.assertEquals(resultType, it.result)
        Assertions.assertNotEquals(false, it.finished)
    }

    @Test
    fun finishGameAfterGameFinishedThrowExceptionTest() = withMatch {
        val resultType = MatchResultType.TEAM_A_WINS
        val result = matchService.finishGame(resultType, matchService.convertToDTO(it))
        Assertions.assertNotNull(result)

        assertThrows<Exception> {
            matchService.finishGame(resultType, matchService.convertToDTO(it))
        }
    }

    @Test
    fun findTest() = withMatch {
        val id = it.id
        val matchDTO = matchService.getById(id)
        Assertions.assertNotNull(matchDTO)
        println(matchDTO)
    }

    @Test
    fun pageTest() = withMatches(25) {
        val pageOne = matchService.findAll(1)
        assert(pageOne.size == 10)

        val pageTwo = matchService.findAll(2)
        assert(!(pageOne contentDeepEquals pageTwo))
    }

    @Test
    fun complexScenarioTest() = withMatch { match ->
        withPlayers(4) { players ->
            val result = MatchResultType.TEAM_A_WINS

            val teamA = players.subList(0, 2)
            val teamB = players.subList(2, 4)
            assert(teamA != teamB)

            match.teamA.addAll(teamA)
            match.teamB.addAll(teamB)

            val dto = matchService.convertToDTO(match)
            assert(dto.teamA.size == 2)
            assert(dto.teamB.size == 2)

            matchService.finishGame(result, matchService.convertToDTO(match))
            assert(match.finished)

            val teamAPlayer = match.teamA.first()
            assert(teamAPlayer.rating > 1000)

            val teamBPlayer = match.teamB.first()
            assert(teamBPlayer.rating < 1000)
        }
    }
}
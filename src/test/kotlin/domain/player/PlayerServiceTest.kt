package domain.player

import domain.AbstractDomainTest
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.h2.H2DatabaseTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Test
import server.domain.match.MatchDTO
import server.domain.ranked.RankedPlayerDTO
import javax.transaction.Transactional


@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource::class)
class PlayerServiceTest : AbstractDomainTest() {


    @Test
    @Transactional
    fun createFromDTOTest() {

        val dto = RankedPlayerDTO().apply {
            name = "Markus"
        }

        val rankedPlayer = playerService.create(dto)
        assert(rankedPlayer != null)
        assert(rankedPlayer!!.name == dto.name)

        val newRankedPlayer = playerService.rankedPlayerRepository.findByName(dto.name)
        assert(newRankedPlayer != null)
        assert(newRankedPlayer!!.name == dto.name)

        val all = playerService.getAll(0)
        assert(all.isNotEmpty())

        //Cleanup
        newRankedPlayer.delete()
    }

    @Test
    fun convertToDTOTest() = withPlayer { player ->
        withMatch { match ->
            match.teamA.add(player)
            player.matches.add(match)

            val dto = playerService.convertToDTO(player)
            assert(dto != null)
            assert(dto!!.name == player.name)
            assert(dto.rating == player.rating)
            assert(dto.matches.size == player.matches.map { it.id }.toTypedArray().size)
        }


    }


}
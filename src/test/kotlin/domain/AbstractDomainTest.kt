package domain

import io.smallrye.mutiny.Uni
import server.domain.match.Match
import server.domain.match.MatchDTO
import server.domain.match.MatchServiceImpl
import server.domain.ranked.RankedPlayer
import server.domain.ranked.RankedPlayerServiceImpl
import javax.inject.Inject
import javax.transaction.Transactional

abstract class AbstractDomainTest {

    @Inject
    protected lateinit var matchService: MatchServiceImpl

    @Inject
    protected lateinit var playerService: RankedPlayerServiceImpl

    @Transactional
    fun withMatch(block: (Match) -> Unit) {
        val match = Match()
        match.persist()
        block.invoke(match)
        match.delete()
    }

    @Transactional
    fun withMatches(count: Int = 10, block: (List<Match>) -> Unit) {
        val matches = (0 until count).map { Match() }.onEach { it.persist() }

        block.invoke(matches)

        matches.forEach {
            it.delete()
        }
    }

    @Transactional
    fun withPlayers(count: Int = 2, block: (List<RankedPlayer>) -> Unit) {
        val players = (0 until count).map { RankedPlayer() }.onEach { it.persist() }

        block.invoke(players)

        players.forEach {
            it.delete()
        }
    }

    @Transactional
    fun withPlayer(block: (RankedPlayer) -> Unit) {
        val rankedPlayer = RankedPlayer()
        rankedPlayer.name = "oberstrike123"
        rankedPlayer.persist()
        block.invoke(rankedPlayer)
        rankedPlayer.delete()
    }


}
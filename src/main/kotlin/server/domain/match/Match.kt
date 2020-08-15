package server.domain.match

import elo.IMatch
import elo.MatchResult
import elo.MatchResultType
import io.quarkus.hibernate.orm.panache.PanacheEntity
import server.domain.ranked.RankedPlayer
import javax.persistence.*
import kotlin.math.pow


@Entity
class Match : IMatch<Match, RankedPlayer>, PanacheEntity() {

    @Enumerated(EnumType.STRING)
    override lateinit var result: MatchResultType

    override var version: String = "1.0"

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "MatchPlayerTeamA",
        joinColumns = [
            JoinColumn("match_id")
        ],
        inverseJoinColumns = [
            JoinColumn("rankedPlayer_id")
        ]
    )
    var teamA: MutableSet<RankedPlayer> = mutableSetOf()


    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "MatchPlayer",
        joinColumns = [
            JoinColumn("match_id")
        ],
        inverseJoinColumns = [
            JoinColumn("rankedPlayer_id")
        ]
    )
    var teamB: MutableSet<RankedPlayer> = mutableSetOf()

    override var finished: Boolean = false


    data class Builder(
        var teamA: List<RankedPlayer> = listOf(),
        var teamB: List<RankedPlayer> = listOf(),
        var version: String = "",
        var id: Long = 0
    ) {
        fun teamA(teamA: List<RankedPlayer>) = apply { this.teamA = teamA }
        fun teamB(teamB: List<RankedPlayer>) = apply { this.teamB = teamB }
        fun version(version: String) = apply { this.version = version }
        fun id(id: Long) = apply { this.id = id }

        fun build(): Match {
            val match = Match()
            match.id = id
            match.teamA = this.teamA.toMutableSet()
            match.teamB = this.teamB.toMutableSet()
            teamA.forEach { it.teamAMatches.add(match) }
            teamB.forEach { it.teamBMatches.add(match) }

            return match

        }

    }


    //If the player has won +1 in case of a draw +0.5 in case of a defeat -1
    override fun getScore(player: RankedPlayer, resultType: MatchResultType): Double {
        if (!teamA.contains(player) && !teamB.contains(player))
            throw Exception("Player is not in the game")
        if (teamA.contains(player) && resultType == MatchResultType.TEAM_A_WINS) {
            return 1.0
        } else if (teamB.contains(player) && resultType == MatchResultType.TEAM_B_WINS) {
            return 1.0
        } else if (resultType == MatchResultType.DRAW) {
            return 0.5
        }
        return 0.0
    }


    override fun getOpponentsAverageRating(player: RankedPlayer): Double {
        return if (teamA.contains(player)) teamB.sumByDouble { it.rating } / teamB.size
        else teamA.sumByDouble { it.rating } / teamA.size
    }

    //probability of victory of one player depending on the average team rating of the opponent team
    override fun getWinOdds(playerOneRating: Double, enemyTeamRating: Double): Double {
        return 1.0 / (1 + 10.0.pow(((enemyTeamRating - playerOneRating) / 400)))
    }


    @Throws(GameIsAlreadyOverException::class)
    override fun gameIsOver(resultType: MatchResultType): MatchResult<RankedPlayer> {
        if (finished)
            throw GameIsAlreadyOverException()
        result = resultType
        finished = true

        val newRatings = mutableListOf<Double>()
        val allPlayers = teamA.plus(teamB)

        //Calculate all new ratings
        for (player in allPlayers) {
            val enemyOddRating = getOpponentsAverageRating(player)
            val odd = getWinOdds(player.rating, enemyOddRating)
            newRatings.add(player.rating + player.getK() * (getScore(player, resultType) - odd))
        }

        allPlayers.forEachIndexed { index, iRankedUser -> iRankedUser.rating = newRatings[index] }



        if (resultType == MatchResultType.DRAW) {
            return MatchResult(result = resultType)
        }
        return MatchResult(
            winningTeam = (if (resultType == MatchResultType.TEAM_A_WINS) teamA else teamB).toList(),
            losingTeam = (if (resultType == MatchResultType.TEAM_A_WINS) teamB else teamA).toList(),
            result = resultType
        )
    }


    class GameIsAlreadyOverException : Exception("Game is already over")

}


package server.domain

import elo.IMatch
import elo.IRankedPlayer
import elo.MatchResult
import elo.MatchResultType
import org.hibernate.mapping.Join
import javax.persistence.*
import kotlin.math.pow


@Entity
class Match : IMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "MatchPlayerTeamA",
        joinColumns = [
            JoinColumn("Match_id")
        ],
        inverseJoinColumns = [
            JoinColumn("Player_id")
        ]
    )
    var teamA: List<RankedPlayer> = listOf()


    @ManyToMany
    var teamB: List<RankedPlayer> = listOf()

    override var finished: Boolean = false

    init {
        teamA.plus(teamB).forEach { it.addMatch(this) }
    }

    data class Builder(
        var teamA: List<RankedPlayer>? = null,
        var teamB: List<RankedPlayer>? = null,
        var id: Long = 0
    ) {
        fun teamA(teamA: List<RankedPlayer>) = apply { this.teamA = teamA }
        fun teamB(teamB: List<RankedPlayer>) = apply { this.teamB = teamB }

        fun id(id: Long) = apply { this.id = id }

        fun build(): IMatch? {
            val match = Match()
            match.id = id
            if (teamA != null)
                match.teamA = this.teamA!!
            if (teamB != null)
                match.teamB = this.teamB!!

            return match

        }

    }


    //If the player has won +1 in case of a draw +0.5 in case of a defeat -1
    override fun getScore(player: IRankedPlayer, resultType: MatchResultType): Double {
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


    override fun getOpponentsAverageRating(player: IRankedPlayer): Double {
        return if (teamA.contains(player)) teamB.sumByDouble { it.rating } / teamB.size
        else teamA.sumByDouble { it.rating } / teamA.size
    }

    //probability of victory of one player depending on the average team rating of the opponent team
    override fun getWinOdds(playerOneRating: Double, enemyTeamRating: Double): Double {
        return 1.0 / (1 + 10.0.pow(((enemyTeamRating - playerOneRating) / 400)))
    }


    @Throws(GameIsAlreadyOverException::class)
    override fun gameIsOver(resultType: MatchResultType): MatchResult<IRankedPlayer> {
        if (finished)
            throw GameIsAlreadyOverException()

        val newRatings = mutableListOf<Double>()
        val allPlayers = teamA.plus(teamB)

        //Calculate all new ratings
        for (player in allPlayers) {
            val enemyOddRating = getOpponentsAverageRating(player)
            val odd = getWinOdds(player.rating, enemyOddRating)
            newRatings.add(player.rating + player.getK() * (getScore(player, resultType) - odd))
        }

        allPlayers.forEachIndexed { index, iRankedUser -> iRankedUser.rating = newRatings[index] }
        finished = true

        if (resultType == MatchResultType.DRAW) {
            return MatchResult(result = resultType)
        }
        return MatchResult(
            winningTeam = (if (resultType == MatchResultType.TEAM_A_WINS) teamA else teamB) as List<IRankedPlayer>,
            losingTeam = (if (resultType == MatchResultType.TEAM_A_WINS) teamB else teamA) as List<IRankedPlayer>,
            result = resultType
        )
    }


    class GameIsAlreadyOverException : Exception("Game is already over")

}


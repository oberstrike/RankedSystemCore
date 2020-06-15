package elo

import java.util.*
import kotlin.math.pow

interface IMatch {

    var finished: Boolean

    var id: UUID

    fun getOpponentsAverageRating(player: IRankedPlayer): Double

    fun getScore(player: IRankedPlayer, result: MatchResult): Double

    fun getWinOdds(playerOneRating: Double, enemyTeamRating: Double): Double

    fun gameIsOver(result: MatchResult)
}

class Match private constructor(
    private val teamA: Array<IRankedPlayer> = arrayOf(),
    private val teamB: Array<IRankedPlayer> = arrayOf()
) : IMatch {
    
    override var id: UUID = UUID.randomUUID()

    override var finished: Boolean = false

    init {
        teamA.plus(teamB).forEach { it.addMatch(this) }
    }

    data class Builder(
        var teamA: Array<IRankedPlayer>? = null,
        var teamB: Array<IRankedPlayer>? = null
    ) {
        fun teamA(teamA: Array<IRankedPlayer>) = apply { this.teamA = teamA }
        fun teamB(teamB: Array<IRankedPlayer>) = apply { this.teamB = teamB }
        fun build(): IMatch? {
            if (teamA == null || teamB == null) {
                return null
            } else if (teamA!!.isEmpty() || teamB!!.isEmpty()) {
                return null
            }
            return Match(teamA!!, teamB!!)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Builder

            if (teamA != null) {
                if (other.teamA == null) return false
                if (!teamA!!.contentEquals(other.teamA!!)) return false
            } else if (other.teamA != null) return false
            if (teamB != null) {
                if (other.teamB == null) return false
                if (!teamB!!.contentEquals(other.teamB!!)) return false
            } else if (other.teamB != null) return false

            return true
        }

        override fun hashCode(): Int {
            var result1 = teamA?.contentHashCode() ?: 0
            result1 = 31 * result1 + (teamB?.contentHashCode() ?: 0)
            return result1
        }
    }


    //If the player has won +1 in case of a draw +0.5 in case of a defeat -1
    override fun getScore(player: IRankedPlayer, result: MatchResult): Double {
        if (!teamA.contains(player) && !teamB.contains(player))
            throw Exception("Player is not in the game")
        if (teamA.contains(player) && result == MatchResult.TEAM_A_WINS) {
            return 1.0
        } else if (teamB.contains(player) && result == MatchResult.TEAM_B_WINS) {
            return 1.0
        } else if (result == MatchResult.DRAW) {
            return 0.5
        }
        return 0.0
    }


    override fun getOpponentsAverageRating(player: IRankedPlayer): Double {
        return if (teamA.contains(player)) teamB.sumByDouble { it.rating } / teamB.size
        else teamA.sumByDouble { it.rating } / teamA.size
    }

    //probability of victory of one player depending on the average team rating of the opponent
    override fun getWinOdds(playerOneRating: Double, enemyTeamRating: Double): Double {
        return 1.0 / (1 + 10.0.pow(((enemyTeamRating - playerOneRating) / 400)))
    }

    @Throws(GameIsAlreadyOverException::class)
    override fun gameIsOver(result: MatchResult) {
        if(finished)
            throw GameIsAlreadyOverException()

        val newRatings = mutableListOf<Double>()
        val allPlayers = teamA.plus(teamB)

        for (player in allPlayers) {
            val enemyOddRating = getOpponentsAverageRating(player)
            val odd = getWinOdds(player.rating, enemyOddRating)
            newRatings.add(player.rating + player.getK() * (getScore(player, result) - odd))
        }

        allPlayers.forEachIndexed { index, iRankedUser -> iRankedUser.rating = newRatings[index] }

        finished = true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Match

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


    class GameIsAlreadyOverException: Exception("Game is already over")

}

enum class MatchResult {
    TEAM_A_WINS,
    TEAM_B_WINS,
    DRAW
}
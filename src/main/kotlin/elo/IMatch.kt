package elo


interface IMatch<S : IMatch<S, T>, T : IRankedPlayer<T, S>> {

    var result: MatchResultType

    var finished: Boolean

    var version: String

    fun getOpponentsAverageRating(player: T): Double

    fun getScore(player: T, resultType: MatchResultType): Double

    fun getWinOdds(playerOneRating: Double, enemyTeamRating: Double): Double

    fun gameIsOver(resultType: MatchResultType): MatchResult<T>
}

enum class MatchResultType {
    TEAM_A_WINS,
    TEAM_B_WINS,
    DRAW
}

data class MatchResult<T : IRankedPlayer<*, *>>(
    var winningTeam: List<T>? = null,
    var losingTeam: List<T>? = null,
    var result: MatchResultType
)

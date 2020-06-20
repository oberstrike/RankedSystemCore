package elo


interface IMatch {

    var finished: Boolean

    fun getOpponentsAverageRating(player: IRankedPlayer): Double

    fun getScore(player: IRankedPlayer, resultType: MatchResultType): Double

    fun getWinOdds(playerOneRating: Double, enemyTeamRating: Double): Double

    fun gameIsOver(resultType: MatchResultType): MatchResult<IRankedPlayer>
}

enum class MatchResultType {
    TEAM_A_WINS,
    TEAM_B_WINS,
    DRAW
}

data class MatchResult<T : IRankedPlayer>(
    var winningTeam: List<T>? = null,
    var losingTeam: List<T>? = null,
    var result: MatchResultType
)

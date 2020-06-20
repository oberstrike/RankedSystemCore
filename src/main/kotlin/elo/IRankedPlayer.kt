package elo

/**
 * Type: Interface
 * Name: IRankedPlayer
 * Aktive Person, die an Spielen teilnimmt.
 * Besitzt ein Rating, welches das aktuelle Elo-Niveau darstellt
 * + eine Match-History um zu verhindern, dass Spiele 2 mal gespielt werden
 */
interface IRankedPlayer {
    var name: String
    var rating: Double
    val matches: List<IMatch>

    fun getK(): Int

    fun addMatch(match: IMatch)
}


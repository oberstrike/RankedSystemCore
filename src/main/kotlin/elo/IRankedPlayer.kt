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

class RankedPlayer(
    override var name: String,
    override var rating: Double = 1000.0,
    override val matches: MutableList<IMatch> = mutableListOf()
) : IRankedPlayer {

    override fun addMatch(match: IMatch) {
        matches.add(match)
    }

    //K-Faktor: https://de.wikipedia.org/wiki/Elo-Zahl
    override fun getK(): Int = if (matches.size > 15) 16 else 25
}
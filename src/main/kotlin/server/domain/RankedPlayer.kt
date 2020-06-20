package server.domain

import elo.IMatch
import elo.IRankedPlayer
import javax.persistence.*


@Entity
class RankedPlayer : IRankedPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    override var name: String = ""

    override var rating: Double = 1000.0


    @ManyToMany
    override var matches: MutableList<Match> = mutableListOf()

    override fun addMatch(match: IMatch) {
        matches.add(match as Match)
    }

    //K-Faktor: https://de.wikipedia.org/wiki/Elo-Zahl
    override fun getK(): Int = if (matches.size > 15) 16 else 25


    data class Builder(
        var id: Long? = null,
        var name: String? = null,
        var rating: Double = 1000.0
    ) {
        fun id(id: Long) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun rating(rating: Double) = apply { this.rating = rating }

        fun build(): RankedPlayer {
            val rankedPlayer = RankedPlayer()
            if (id != null) rankedPlayer.id = id!!
            if (name != null) rankedPlayer.name = name!!
            rankedPlayer.rating = rating
            return rankedPlayer
        }


    }

}
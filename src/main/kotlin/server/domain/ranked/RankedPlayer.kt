package server.domain.ranked

import elo.IRankedPlayer
import io.quarkus.hibernate.orm.panache.PanacheEntity
import server.domain.match.Match
import javax.persistence.*


@Entity
class RankedPlayer : IRankedPlayer<RankedPlayer, Match>, PanacheEntity() {

    @ManyToMany(mappedBy = "teamA")
    override var matches: MutableSet<Match> = mutableSetOf()

    override fun addMatch(match: Match) {
        matches.add(match)
    }

    override var name: String = ""

    override var rating: Double = 1000.0


    //K-Faktor: https://de.wikipedia.org/wiki/Elo-Zahl
    override fun getK(): Int = if (matches.size > 15) 16 else 25


    data class Builder(
        var id: Long? = null,
        var name: String? = null,
        var rating: Double = 1000.0
    ) {
        fun id(id: Long?) = apply { this.id = id }
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
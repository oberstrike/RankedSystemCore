package server.domain.match

import server.domain.ranked.RankedPlayerDTO

data class MatchDTO(
    var id: Long = 0,
    var finished: Boolean = false,
    var teamA: Array<Long> = arrayOf(),
    var teamB: Array<Long> = arrayOf(),
    var version: String = "1.0"
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MatchDTO

        if (id != other.id) return false
        if (finished != other.finished) return false
        if (!teamA.contentEquals(other.teamA)) return false
        if (!teamB.contentEquals(other.teamB)) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + finished.hashCode()
        result = 31 * result + teamA.contentHashCode()
        result = 31 * result + teamB.contentHashCode()
        result = 31 * result + version.hashCode()

        return result
    }
}
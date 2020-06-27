package server.domain.ranked

data class RankedPlayerDTO(
    var id: Long = 0,
    var name: String = "",
    var rating: Double = 1000.0,
    var matches: Array<Long> = emptyArray()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RankedPlayerDTO

        if (id != other.id) return false
        if (name != other.name) return false
        if (rating != other.rating) return false
        if (!matches.contentEquals(other.matches)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + rating.hashCode()
        result = 31 * result + matches.contentHashCode()
        return result
    }

}
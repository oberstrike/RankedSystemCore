package server.domain.ranked

data class RankedPlayerDTO(
    var id: Long,
    var name: String,
    var rating: Double,
    var matches: List<Long>
) {

}
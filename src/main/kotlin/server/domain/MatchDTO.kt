package server.domain

data class MatchDTO(
    var id: Long = 0,
    var finished: Boolean = false,
    var teamA: List<RankedPlayerDTO> = listOf(),
    var teamB: List<RankedPlayerDTO> = listOf()
)
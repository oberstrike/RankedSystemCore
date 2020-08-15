package server.domain.queue

import elo.GameQueueType

data class GameQueueDTO(
    var id: Long = 0,
    var players: Array<Long> = arrayOf(),
    var type: GameQueueType = GameQueueType.TwoVsTwo
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameQueueDTO

        if (id != other.id) return false
        if (!players.contentEquals(other.players)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + players.contentHashCode()
        return result
    }

}
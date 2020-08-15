package server.domain.queue

import elo.GameQueueType
import server.domain.ranked.RankedPlayer

class GameQueue(
    private val gameQueueType: GameQueueType,
    private val onGameQueueIsFullListener: OnGameQueueIsFullListener
) {

    @Synchronized
    fun addPlayer(rankedPlayer: RankedPlayer) {
        players.add(rankedPlayer)

        val isOver = when (gameQueueType) {
            GameQueueType.OneVsOne -> players.size == 2
            GameQueueType.TwoVsTwo -> players.size == 4
        }

        if (isOver) {
            onGameQueueIsFullListener.onGameQueueIsFull(players)
            players.clear()
        }
    }

    val players: MutableSet<RankedPlayer> = mutableSetOf()


    interface OnGameQueueIsFullListener {
        fun onGameQueueIsFull(rankedPlayers: Set<RankedPlayer>)
    }
}


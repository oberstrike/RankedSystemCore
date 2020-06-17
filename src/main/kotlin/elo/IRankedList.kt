package elo

interface IRankedList {
    fun addRankedPlayer(player: IRankedPlayer)

    fun getRankedPlayerByName(name: String): IRankedPlayer?
}

class RankedList : IRankedList {

    val players: MutableList<IRankedPlayer> = mutableListOf()

    override fun addRankedPlayer(player: IRankedPlayer) {
        players.add(player)
    }

    override fun getRankedPlayerByName(name: String): IRankedPlayer? {
        return players.find { it.name == name }
    }
}
package elo

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.Assertions
import server.domain.queue.GameQueue
import server.domain.ranked.RankedPlayer

class OnGameQueueIsFullListenerExample : GameQueue.OnGameQueueIsFullListener {
    override fun onGameQueueIsFull(rankedPlayers: Set<RankedPlayer>) {
        println("Game is full!")
    }
}

class QueueTest : Spek({

    describe("Starte eine Warteschlange für 1 gegen 1") {
        val queue = GameQueue(
            GameQueueType.OneVsOne,
            OnGameQueueIsFullListenerExample()
        )

        describe("Ein Spieler will der Warteschlange beitreten.") {
            val playerOne = RankedPlayer.Builder().name("oberstrike").id(1).build()

            it("Füge einen Spieler hinzu") {
                queue.addPlayer(playerOne)

                it("Es ist nun ein Spieler vorhanden") {
                    Assertions.assertTrue(queue.players.contains(playerOne))
                }
            }

            val playerTwo = RankedPlayer.Builder().name("Scouty").id(2).build()

            it("Füge noch einen Spieler hinzu") {
                queue.addPlayer(playerTwo)

                it("Es sind nun keine Spieler mehr vorhanden, weil die Queue voll war"){
                    Assertions.assertTrue(queue.players.size == 0)
                }

            }


        }


    }


})
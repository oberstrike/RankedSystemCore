package elo

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.Assertions
import server.domain.match.Match
import server.domain.ranked.RankedPlayer

class MatchTest : Spek({
    describe("A Match is starting") {

        val playerOne = RankedPlayer.Builder().name("PlayerOne").build()
        val playerTwo = RankedPlayer.Builder().name("PlayerTwo").build()
        val playerThree = RankedPlayer.Builder().name("PlayerThree").rating(1200.0).build()

        val match: Match? = Match.Builder()
            .teamA(listOf(playerOne, playerThree))
            .teamB(listOf(playerTwo))
            .build()

        val result = MatchResultType.TEAM_B_WINS

        Assertions.assertNotNull(match)

        describe("Compare scores") {
            val playerOneScore = match?.getScore(playerOne, result)
            val playerTwoScore = match?.getScore(playerTwo, result)

            it("Scores are not equal") {
                Assertions.assertNotEquals(playerOneScore, playerTwoScore)
            }
        }

        describe("Test if the results are right") {
            match?.gameIsOver(result)
            it("Ratings are not equal") {
                Assertions.assertNotEquals(playerOne.rating, playerTwo.rating)
            }

            it("Player one wins") {
                Assertions.assertTrue(playerTwo.rating > playerOne.rating)
            }

        }


    }

})
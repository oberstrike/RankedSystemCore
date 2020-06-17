package elo

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.Assertions

class MatchTest : Spek({
    describe("A Match is starting") {

        val playerOne = RankedPlayer("PlayerOne")
        val playerTwo = RankedPlayer("PlayerTwo")
        val playerThree = RankedPlayer("PlayerThree", rating = 1200.0)

        val match: IMatch? = Match.Builder()
            .teamA(arrayOf(playerOne, playerThree))
            .teamB(arrayOf(playerTwo))
            .build()

        val result = MatchResult.TEAM_B_WINS

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

            it("Player has played this game") {
                Assertions.assertTrue(playerOne.matches.contains(match))
            }
        }




    }

})
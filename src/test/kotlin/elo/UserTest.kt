package elo

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.Assertions.assertEquals

class UserTest : Spek({

    describe("A string"){
        var string = "Calculator"
        describe("concat"){
            string += " xyz"
            it("Produces the right string"){
                 assertEquals("Calculator xyz", string)
            }
        }
    }



})
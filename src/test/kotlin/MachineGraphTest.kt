package ru.ought.greg_recipe_balancer

import org.spekframework.spek2.Spek
import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect


@Suppress("LocalVariableName")
object MachineGraphTest : Spek({
    group("basic tests") {
        val Na by memoized { Ingredient("Na") }
        val H2O by memoized { Ingredient("H2O") }
        val NaOH by memoized { Ingredient("NaOH") }
        val H2 by memoized { Ingredient("H2") }

        val recipe by memoized {
            Recipe(
                listOf(Stack(Na, 1), Stack(H2O, 3000)),
                listOf(Stack(NaOH, 3), Stack(H2, 1000)),
                30, 29f
            )
        }

        test("basic test") {
            val sut = MachineGraph()


        }
    }
})

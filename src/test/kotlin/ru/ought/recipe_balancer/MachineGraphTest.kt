package ru.ought.recipe_balancer

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toBeWithErrorTolerance
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek


@Suppress("LocalVariableName")
object MachineGraphTest : Spek({
    group("basic tests") {
        val a by memoized { Ingredient("A") }
        val b by memoized { Ingredient("B") }
        val c by memoized { Ingredient("C") }
        val d by memoized { Ingredient("D") }
        val e by memoized { Ingredient("E") }
        val f by memoized { Ingredient("F") }

        val recipeABC by memoized {
            Recipe(
                listOf(Stack(a, 1)),
                listOf(Stack(b, 3), Stack(c, 2)),
                30f, 1f
            )
        }

        val recipeCDEF by memoized {
            Recipe(
                listOf(Stack(c, 4), Stack(d, 1)),
                listOf(Stack(e, 2), Stack(f, 3000)),
                30f, 1f
            )
        }

        test("can be linked") {
            val machineABC = MachineStack(recipeABC)
            val machineCDEF = MachineStack(recipeCDEF)
            val sut = MachineGraph()

            sut.link(machineABC, machineCDEF, c)

            expect(sut.links).toBe(
                mapOf(
                    machineABC to listOf(
                        MachineLink(machineABC, machineCDEF, c)
                    )
                )
            )
        }

        test("can balance forward graph with two nodes") {
            val machineABC = MachineStack(recipeABC)
            val machineCDEF = MachineStack(recipeCDEF)
            val sut = MachineGraph()
            sut.link(machineABC, machineCDEF, c)

            sut.balanceForward()

            expect(machineABC.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineCDEF.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
        }
    }
})

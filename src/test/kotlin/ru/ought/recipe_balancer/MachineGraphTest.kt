package ru.ought.recipe_balancer

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toBeWithErrorTolerance
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


@Suppress("LocalVariableName")
object MachineGraphTest : Spek({
    describe("basic tests") {
        val a by memoized { Ingredient("A") }
        val b by memoized { Ingredient("B") }
        val c by memoized { Ingredient("C") }
        val d by memoized { Ingredient("D") }
        val e by memoized { Ingredient("E") }
        val f by memoized { Ingredient("F") }

        val recipeAB by memoized {
            Recipe(
                listOf(Stack(a, 1)),
                listOf(Stack(b, 1)),
                30f, 1f
            )
        }

        val recipeBCD by memoized {
            Recipe(
                listOf(Stack(b, 2)),
                listOf(Stack(c, 1), Stack(d, 2)),
                30f, 1f
            )
        }

        val recipeDEF by memoized {
            Recipe(
                listOf(Stack(d, 1), Stack(e, 3)),
                listOf(Stack(f, 4)),
                30f, 1f
            )
        }

        val recipeAE by memoized {
            Recipe(
                listOf(Stack(a, 1)),
                listOf(Stack(e, 2)),
                30f, 1f
            )
        }

        val recipeCA by memoized {
            Recipe(
                listOf(Stack(c, 1)),
                listOf(Stack(a, 1)),
                30f, 1f
            )
        }

        it("can be linked") {
            val machineAB = MachineStack(recipeAB)
            val machineBCD = MachineStack(recipeBCD)
            val sut = MachineGraph()

            sut.link(machineAB, machineBCD, b)

            expect(sut.forwardLinks).toBe(
                mapOf(
                    machineAB to listOf(
                        MachineLink(machineAB, machineBCD, b)
                    )
                )
            )
            expect(sut.backwardLinks).toBe(
                mapOf(
                    machineBCD to listOf(
                        MachineLink(machineAB, machineBCD, b)
                    )
                )
            )
        }

        it("can balance forward graph with two nodes") {
            val machineAB = MachineStack(recipeAB)
            val machineBCD = MachineStack(recipeBCD)
            val sut = MachineGraph()
            sut.link(machineAB, machineBCD, b)

            val result = sut.balanceForward()

            expect(result).toBe(true)
            expect(machineAB.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineBCD.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
        }

        it("does not change when balancing forward and not needed") {
            val machineAB = MachineStack(recipeAB, 3)
            val machineBCD = MachineStack(recipeBCD)
            val sut = MachineGraph()
            sut.link(machineAB, machineBCD, b)

            val result = sut.balanceForward()

            expect(result).toBe(false)
            expect(machineAB.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineBCD.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("can balance backward graph with two nodes") {
            val machineBCD = MachineStack(recipeBCD)
            val machineDEF = MachineStack(recipeDEF)
            val sut = MachineGraph()
            sut.link(machineBCD, machineDEF, d)

            val result = sut.balanceBackward()

            expect(result).toBe(true)
            expect(machineBCD.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
            expect(machineDEF.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("does not change when balancing backward and not needed") {
            val machineBCD = MachineStack(recipeBCD)
            val machineDEF = MachineStack(recipeDEF, 3)
            val sut = MachineGraph()
            sut.link(machineBCD, machineDEF, d)

            val result = sut.balanceBackward()

            expect(result).toBe(false)
            expect(machineBCD.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineDEF.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("can balance forward V-graph with three nodes") {
            val machineBCD = MachineStack(recipeBCD)
            val machineAE = MachineStack(recipeAE)
            val machineDEF = MachineStack(recipeDEF, 2)
            val sut = MachineGraph()
            sut.link(machineBCD, machineDEF, d)
            sut.link(machineAE, machineDEF, e)

            sut.balanceForward()

            expect(machineBCD.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineAE.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineDEF.boundedRatio).toBeWithErrorTolerance(0.33f, 0.01f)
        }

        it("can balance backward ^-graph with three nodes") {
            val machineBCD = MachineStack(recipeBCD)
            val machineCA = MachineStack(recipeCA)
            val machineDEF = MachineStack(recipeDEF)
            val sut = MachineGraph()
            sut.link(machineBCD, machineCA, c)
            sut.link(machineBCD, machineDEF, d)

            sut.balanceBackward()

            expect(machineBCD.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
            expect(machineCA.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineDEF.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("can full balance V-graph with three nodes") {
            val machineBCD = MachineStack(recipeBCD)
            val machineAE = MachineStack(recipeAE)
            val machineDEF = MachineStack(recipeDEF, 2)
            val sut = MachineGraph()
            sut.link(machineBCD, machineDEF, d)
            sut.link(machineAE, machineDEF, e)

            sut.balance()

            expect(machineBCD.boundedRatio).toBeWithErrorTolerance(0.33f, 0.01f)
            expect(machineAE.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineDEF.boundedRatio).toBeWithErrorTolerance(0.33f, 0.01f)
        }

        it("can full balance ^-graph with three nodes") {
            val machineBCD = MachineStack(recipeBCD)
            val machineCA = MachineStack(recipeCA)
            val machineDEF = MachineStack(recipeDEF)
            val sut = MachineGraph()
            sut.link(machineBCD, machineCA, c)
            sut.link(machineBCD, machineDEF, d)

            sut.balance()

            expect(machineBCD.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
            expect(machineCA.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
            expect(machineDEF.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("can full balance complex graph") {
            val machineAB = MachineStack(recipeAB)
            val machineBCD = MachineStack(recipeBCD)
            val machineCA = MachineStack(recipeCA)
            val machineAE = MachineStack(recipeAE)
            val machineDEF = MachineStack(recipeDEF)
            val sut = MachineGraph()
            sut.link(machineAB, machineBCD, b)
            sut.link(machineBCD, machineCA, c)
            sut.link(machineAE, machineDEF, e)
            sut.link(machineBCD, machineDEF, d)

            sut.balance()

            expect(machineAB.boundedRatio).toBeWithErrorTolerance(0.66f, 0.01f)
            expect(machineBCD.boundedRatio).toBeWithErrorTolerance(0.33f, 0.01f)
            expect(machineCA.boundedRatio).toBeWithErrorTolerance(0.33f, 0.01f)
            expect(machineAE.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineDEF.boundedRatio).toBeWithErrorTolerance(0.66f, 0.01f)
        }
    }
})

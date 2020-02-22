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

        val recipeA_B by memoized {
            Recipe(
                listOf(Stack(a, 1)),
                listOf(Stack(b, 1)),
                30f, 1f
            )
        }

        val recipeB_CD by memoized {
            Recipe(
                listOf(Stack(b, 2)),
                listOf(Stack(c, 1), Stack(d, 2)),
                30f, 1f
            )
        }

        val recipeDE_F by memoized {
            Recipe(
                listOf(Stack(d, 1), Stack(e, 3)),
                listOf(Stack(f, 4)),
                30f, 1f
            )
        }

        val recipeA_E by memoized {
            Recipe(
                listOf(Stack(a, 1)),
                listOf(Stack(e, 2)),
                30f, 1f
            )
        }

        val recipeC_A by memoized {
            Recipe(
                listOf(Stack(c, 1)),
                listOf(Stack(a, 1)),
                30f, 1f
            )
        }

        it("can be linked") {
            val machineA_B = MachineStack(recipeA_B)
            val machineB_CD = MachineStack(recipeB_CD)
            val sut = MachineGraph()

            sut.link(machineA_B, machineB_CD, b)

            expect(sut.forwardLinks).toBe(
                mapOf(
                    machineA_B to listOf(
                        MachineLink(machineA_B, machineB_CD, b)
                    )
                )
            )
            expect(sut.backwardLinks).toBe(
                mapOf(
                    machineB_CD to listOf(
                        MachineLink(machineA_B, machineB_CD, b)
                    )
                )
            )
        }

        it("can balance forward graph with two nodes") {
            val machineA_B = MachineStack(recipeA_B)
            val machineB_CD = MachineStack(recipeB_CD)
            val sut = MachineGraph()
            sut.link(machineA_B, machineB_CD, b)

            val result = sut.balanceForward()

            expect(result).toBe(true)
            expect(machineA_B.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineB_CD.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
        }

        it("does not change when balancing forward and not needed") {
            val machineA_B = MachineStack(recipeA_B, 3)
            val machineB_CD = MachineStack(recipeB_CD)
            val sut = MachineGraph()
            sut.link(machineA_B, machineB_CD, b)

            val result = sut.balanceForward()

            expect(result).toBe(false)
            expect(machineA_B.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineB_CD.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("can balance backward graph with two nodes") {
            val machineB_CD = MachineStack(recipeB_CD)
            val machineDE_F = MachineStack(recipeDE_F)
            val sut = MachineGraph()
            sut.link(machineB_CD, machineDE_F, d)

            val result = sut.balanceBackward()

            expect(result).toBe(true)
            expect(machineB_CD.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
            expect(machineDE_F.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("does not change when balancing backward and not needed") {
            val machineB_CD = MachineStack(recipeB_CD)
            val machineDE_F = MachineStack(recipeDE_F, 3)
            val sut = MachineGraph()
            sut.link(machineB_CD, machineDE_F, d)

            val result = sut.balanceBackward()

            expect(result).toBe(false)
            expect(machineB_CD.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineDE_F.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("can balance forward V-graph with three nodes") {
            val machineB_CD = MachineStack(recipeB_CD)
            val machineA_E = MachineStack(recipeA_E)
            val machineDE_F = MachineStack(recipeDE_F, 2)
            val sut = MachineGraph()
            sut.link(machineB_CD, machineDE_F, d)
            sut.link(machineA_E, machineDE_F, e)

            sut.balanceForward()

            expect(machineB_CD.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineA_E.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineDE_F.boundedRatio).toBeWithErrorTolerance(0.33f, 0.01f)
        }

        it("can balance backward ^-graph with three nodes") {
            val machineB_CD = MachineStack(recipeB_CD)
            val machineC_A = MachineStack(recipeC_A)
            val machineDE_F = MachineStack(recipeDE_F)
            val sut = MachineGraph()
            sut.link(machineB_CD, machineC_A, c)
            sut.link(machineB_CD, machineDE_F, d)

            sut.balanceBackward()

            expect(machineB_CD.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
            expect(machineC_A.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineDE_F.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("can full balance V-graph with three nodes") {
            val machineB_CD = MachineStack(recipeB_CD)
            val machineA_E = MachineStack(recipeA_E)
            val machineDE_F = MachineStack(recipeDE_F, 2)
            val sut = MachineGraph()
            sut.link(machineB_CD, machineDE_F, d)
            sut.link(machineA_E, machineDE_F, e)

            sut.balance()

            expect(machineB_CD.boundedRatio).toBeWithErrorTolerance(0.33f, 0.01f)
            expect(machineA_E.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineDE_F.boundedRatio).toBeWithErrorTolerance(0.33f, 0.01f)
        }

        it("can full balance ^-graph with three nodes") {
            val machineB_CD = MachineStack(recipeB_CD)
            val machineC_A = MachineStack(recipeC_A)
            val machineDE_F = MachineStack(recipeDE_F)
            val sut = MachineGraph()
            sut.link(machineB_CD, machineC_A, c)
            sut.link(machineB_CD, machineDE_F, d)

            sut.balance()

            expect(machineB_CD.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
            expect(machineC_A.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
            expect(machineDE_F.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("can full balance complex graph") {
            val machineA_B = MachineStack(recipeA_B)
            val machineB_CD = MachineStack(recipeB_CD)
            val machineC_A = MachineStack(recipeC_A)
            val machineA_E = MachineStack(recipeA_E)
            val machineDE_F = MachineStack(recipeDE_F)
            val sut = MachineGraph()
            sut.link(machineA_B, machineB_CD, b)
            sut.link(machineB_CD, machineC_A, c)
            sut.link(machineA_E, machineDE_F, e)
            sut.link(machineB_CD, machineDE_F, d)

            sut.balance()

            expect(machineA_B.boundedRatio).toBeWithErrorTolerance(0.66f, 0.01f)
            expect(machineB_CD.boundedRatio).toBeWithErrorTolerance(0.33f, 0.01f)
            expect(machineC_A.boundedRatio).toBeWithErrorTolerance(0.33f, 0.01f)
            expect(machineA_E.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(machineDE_F.boundedRatio).toBeWithErrorTolerance(0.66f, 0.01f)
        }
    }
    describe("real tests") {
        it("balances O2 line") {
            val Cobblestone = Ingredient("Cobblestone")
            val Gravel = Ingredient("Gravel")
            val Sand = Ingredient("Sand")
            val Glass = Ingredient("Glass")

            val rockBreaker = MachineStack(
                Recipe(
                    listOf(),
                    listOf(Stack(Cobblestone, 1)),
                    32f, 0.8f, "Rock Breaker"
                ), 1
            )
            val hammer1 = MachineStack(
                Recipe(
                    listOf(Stack(Cobblestone, 1)),
                    listOf(Stack(Gravel, 1)),
                    16f, 0.5f, "Hammer"
                ), 1
            )
            val hammer2 = MachineStack(
                Recipe(
                    listOf(Stack(Gravel, 1)),
                    listOf(Stack(Sand, 1)),
                    16f, 0.5f, "Hammer"
                ), 1
            )
            val microwave = MachineStack(
                Recipe(
                    listOf(Stack(Sand, 1)),
                    listOf(Stack(Glass, 1)),
                    4f, 1.6f, "Microwave"
                ), 1
            )

            val graph = MachineGraph()
            graph.link(rockBreaker, hammer1, Cobblestone)
            graph.link(hammer1, hammer2, Gravel)
            graph.link(hammer2, microwave, Sand)

            graph.balance()

            expect(rockBreaker.outputStream.getValue(Cobblestone)).toBeWithErrorTolerance(0.63f, 0.01f)
            expect(hammer1.outputStream.getValue(Gravel)).toBeWithErrorTolerance(0.63f, 0.01f)
            expect(hammer2.outputStream.getValue(Sand)).toBeWithErrorTolerance(0.63f, 0.01f)
            expect(microwave.outputStream.getValue(Glass)).toBeWithErrorTolerance(0.63f, 0.01f)
        }
    }
})

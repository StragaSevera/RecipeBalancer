package ru.ought.recipe_balancer

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toBeWithErrorTolerance
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


object MachineLinkTest : Spek({
    describe("basic tests") {
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

        it("can bound forward simple chain") {
            val from = MachineStack(recipeABC, 1)
            val to = MachineStack(recipeCDEF, 1)
            val sut = MachineLink(from, to, c)

            val result = sut.boundForward()

            expect(result).toBe(true)
            expect(from.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(to.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
        }

        it("can bound forward simple chain when already bounded") {
            val from = MachineStack(recipeABC, 1)
            val to = MachineStack(recipeCDEF, 1, 0.6f)
            val sut = MachineLink(from, to, c)

            val result = sut.boundForward()

            expect(result).toBe(true)
            expect(from.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(to.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
        }

        it("can bound forward simple chain when no bounds") {
            val from = MachineStack(recipeABC, 4)
            val to = MachineStack(recipeCDEF, 1)
            val sut = MachineLink(from, to, c)

            val result = sut.boundForward()

            expect(result).toBe(false)
            expect(from.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(to.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("can bound forward simple chain when no bounds and already bounded") {
            val from = MachineStack(recipeABC, 4)
            val to = MachineStack(recipeCDEF, 1, 0.6f)
            val sut = MachineLink(from, to, c)

            val result = sut.boundForward()

            expect(result).toBe(false)
            expect(from.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(to.boundedRatio).toBeWithErrorTolerance(0.6f, 0.01f)
        }

        it("can bound backward simple chain") {
            val from = MachineStack(recipeABC, 4)
            val to = MachineStack(recipeCDEF, 1)
            val sut = MachineLink(from, to, c)

            val result = sut.boundBackward()

            expect(result).toBe(true)
            expect(from.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
            expect(to.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("can bound backward simple chain when already bounded") {
            val from = MachineStack(recipeABC, 4, 0.6f)
            val to = MachineStack(recipeCDEF, 1)
            val sut = MachineLink(from, to, c)

            val result = sut.boundBackward()

            expect(result).toBe(true)
            expect(from.boundedRatio).toBeWithErrorTolerance(0.5f, 0.01f)
            expect(to.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("can bound backward simple chain when no bounds") {
            val from = MachineStack(recipeABC, 1)
            val to = MachineStack(recipeCDEF, 1)
            val sut = MachineLink(from, to, c)

            val result = sut.boundBackward()

            expect(result).toBe(false)
            expect(from.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
            expect(to.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }

        it("can bound backward simple chain when already bounded and no bounds") {
            val from = MachineStack(recipeABC, 1, 0.6f)
            val to = MachineStack(recipeCDEF, 1)
            val sut = MachineLink(from, to, c)

            val result = sut.boundBackward()

            expect(result).toBe(false)
            expect(from.boundedRatio).toBeWithErrorTolerance(0.6f, 0.01f)
            expect(to.boundedRatio).toBeWithErrorTolerance(1f, 0.01f)
        }
    }
})
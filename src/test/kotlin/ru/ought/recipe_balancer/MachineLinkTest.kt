package ru.ought.recipe_balancer

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toBeWithErrorTolerance
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


@Suppress("LocalVariableName")
object MachineLinkTest : Spek({
    describe("basic tests") {
        val a by memoized { Ingredient("A") }
        val b by memoized { Ingredient("B") }
        val c by memoized { Ingredient("C") }
        val d by memoized { Ingredient("D") }
        val e by memoized { Ingredient("E") }
        val f by memoized { Ingredient("F") }

        val recipeA_BC by memoized {
            Recipe(
                listOf(Stack(a, 1)),
                listOf(Stack(b, 3), Stack(c, 2)),
                30f, 1f
            )
        }

        val recipeCD_EF by memoized {
            Recipe(
                listOf(Stack(c, 4), Stack(d, 1)),
                listOf(Stack(e, 2), Stack(f, 3000)),
                30f, 1f
            )
        }

        it("can bound underusage input for simple chain") {
            val from = MachineStack(recipeA_BC, 1)
            val to = MachineStack(recipeCD_EF, 1)
            val sut = MachineLink(from, to, c)

            val result = sut.boundUUInput()

            expect(result).toBe(true)
            expect(from.uuOutput).toBeWithErrorTolerance(0f, 0.01f)
            expect(to.uuInput).toBeWithErrorTolerance(0.5f, 0.01f)
        }

        it("can bound underusage input for simple chain when already bounded") {
            val from = MachineStack(recipeA_BC, 1)
            val to = MachineStack(recipeCD_EF, 1, 0.4f)
            val sut = MachineLink(from, to, c)

            val result = sut.boundUUInput()

            expect(result).toBe(true)
            expect(from.uuOutput).toBeWithErrorTolerance(0f, 0.01f)
            expect(to.uuInput).toBeWithErrorTolerance(0.5f, 0.01f)
        }

        it("can bound underusage input for simple chain when no bounds") {
            val from = MachineStack(recipeA_BC, 4)
            val to = MachineStack(recipeCD_EF, 1)
            val sut = MachineLink(from, to, c)

            val result = sut.boundUUInput()

            expect(result).toBe(false)
            expect(from.uuOutput).toBeWithErrorTolerance(0f, 0.01f)
            expect(to.uuInput).toBeWithErrorTolerance(0f, 0.01f)
        }

        it("can bound underusage input for simple chain when no bounds and already bounded") {
            val from = MachineStack(recipeA_BC, 4)
            val to = MachineStack(recipeCD_EF, 1, 0.4f)
            val sut = MachineLink(from, to, c)

            val result = sut.boundUUInput()

            expect(result).toBe(false)
            expect(from.uuOutput).toBeWithErrorTolerance(0f, 0.01f)
            expect(to.uuInput).toBeWithErrorTolerance(0.4f, 0.01f)
        }

        it("can bound underusage output for simple chain") {
            val from = MachineStack(recipeA_BC, 4)
            val to = MachineStack(recipeCD_EF, 1)
            val sut = MachineLink(from, to, c)

            val result = sut.boundUUOutput()

            expect(result).toBe(true)
            expect(from.uuOutput).toBeWithErrorTolerance(0.5f, 0.01f)
            expect(to.uuInput).toBeWithErrorTolerance(0f, 0.01f)
        }

        it("can bound underusage output for simple chain when already bounded") {
            val from = MachineStack(recipeA_BC, 4, 0f, 0.4f)
            val to = MachineStack(recipeCD_EF, 1)
            val sut = MachineLink(from, to, c)

            val result = sut.boundUUOutput()

            expect(result).toBe(true)
            expect(from.uuOutput).toBeWithErrorTolerance(0.5f, 0.01f)
            expect(to.uuInput).toBeWithErrorTolerance(0f, 0.01f)
        }

        it("can bound underusage output for simple chain when no bounds") {
            val from = MachineStack(recipeA_BC, 1)
            val to = MachineStack(recipeCD_EF, 1)
            val sut = MachineLink(from, to, c)

            val result = sut.boundUUOutput()

            expect(result).toBe(false)
            expect(from.uuOutput).toBeWithErrorTolerance(0f, 0.01f)
            expect(to.uuInput).toBeWithErrorTolerance(0f, 0.01f)
        }

        it("can bound underusage output for simple chain when already bounded and no bounds") {
            val from = MachineStack(recipeA_BC, 1, 0f, 0.4f)
            val to = MachineStack(recipeCD_EF, 1)
            val sut = MachineLink(from, to, c)

            val result = sut.boundUUOutput()

            expect(result).toBe(false)
            expect(from.uuOutput).toBeWithErrorTolerance(0.4f, 0.01f)
            expect(to.uuInput).toBeWithErrorTolerance(0f, 0.01f)
        }
    }
})
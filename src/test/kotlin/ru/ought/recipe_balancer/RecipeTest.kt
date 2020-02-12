package ru.ought.recipe_balancer

import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toBeWithErrorTolerance
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RecipeTest : Spek({
    describe("basic tests") {
        val a by memoized { Ingredient("A") }
        val b by memoized { Ingredient("B") }
        val c by memoized { Ingredient("C") }
        val d by memoized { Ingredient("D") }

        it("has input and output") {
            val inputs = listOf(
                Stack(a, 1),
                Stack(b, 3000)
            )
            val outputs = listOf(
                Stack(c, 3),
                Stack(d, 1000)
            )

            val sut = Recipe(inputs, outputs, 32f, 2.5f, "Chemical Reactor")

            expect(sut.inputs).toBe(inputs)
            expect(sut.outputs).toBe(outputs)
            expect(sut.duration).toBe(2.5f)
            expect(sut.energyPerTick).toBeWithErrorTolerance(32f, 0.01f)
            expect(sut.machineName).toBe("Chemical Reactor")
        }

        it("can get default machine name") {
            val inputs = listOf(
                Stack(a, 1),
                Stack(b, 3000)
            )
            val outputs = listOf(
                Stack(c, 3),
                Stack(d, 1000)
            )

            val sut = Recipe(inputs, outputs, 32f, 2.5f)

            expect(sut.machineName).toBe("Common Machine")
        }

        it("calculates energy per recipe run") {
            val inputs = listOf(
                Stack(a, 1),
                Stack(b, 3000)
            )
            val outputs = listOf(
                Stack(c, 3),
                Stack(d, 1000)
            )

            val sut = Recipe(inputs, outputs, 32f, 2.5f)

            expect(sut.durationInTicks).toBe(50)
            expect(sut.energyPerRecipe).toBe(1600)
        }

        it("calculates input stream") {
            val inputs = listOf(
                Stack(a, 1),
                Stack(b, 3000)
            )
            val outputs = listOf(
                Stack(c, 3),
                Stack(d, 1000)
            )
            val sut = Recipe(inputs, outputs, 32f, 2.5f)

            val stream = sut.inputStream

            expect(stream.size).toBe(2)
            expect(stream[a]).notToBeNull().toBeWithErrorTolerance(0.4f, 0.01f)
            expect(stream[b]).notToBeNull().toBeWithErrorTolerance(1200f, 1f)
        }

        it("calculates output stream") {
            val inputs = listOf(
                Stack(a, 1),
                Stack(b, 3000)
            )
            val outputs = listOf(
                Stack(c, 3),
                Stack(d, 1000)
            )
            val sut = Recipe(inputs, outputs, 32f, 2.5f)

            val stream = sut.outputStream

            expect(stream.size).toBe(2)
            expect(stream[c]).notToBeNull().toBeWithErrorTolerance(1.2f, 0.01f)
            expect(stream[d]).notToBeNull().toBeWithErrorTolerance(400f, 1f)
        }
    }
})

package ru.ought.recipe_balancer

import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toBeWithErrorTolerance
import org.spekframework.spek2.Spek
import ch.tutteli.atrium.api.verbs.expect


@Suppress("LocalVariableName")
object RecipeTest : Spek({
    group("basic tests") {
        val Na by memoized { Ingredient("Na") }
        val H2O by memoized { Ingredient("H2O") }
        val NaOH by memoized { Ingredient("NaOH") }
        val H2 by memoized { Ingredient("H2") }

        test("has input and output") {
            val inputs = listOf(
                Stack(Na, 1),
                Stack(H2O, 3000)
            )
            val outputs = listOf(
                Stack(NaOH, 3),
                Stack(H2, 1000)
            )

            val sut = Recipe(inputs, outputs, 30, 30f, "Chemical Reactor")

            expect(sut.inputs).toBe(inputs)
            expect(sut.outputs).toBe(outputs)
            expect(sut.duration).toBe(30)
            expect(sut.energyPerTick).toBeWithErrorTolerance(30f, 0.01f)
            expect(sut.machineName).toBe("Chemical Reactor")
        }

        test("can get default machine name") {
            val inputs = listOf(
                Stack(Na, 1),
                Stack(H2O, 3000)
            )
            val outputs = listOf(
                Stack(NaOH, 3),
                Stack(H2, 1000)
            )

            val sut = Recipe(inputs, outputs, 30, 30f)

            expect(sut.machineName).toBe("Common Machine")
        }

        test("calculates energy per recipe run") {
            val inputs = listOf(
                Stack(Na, 1),
                Stack(H2O, 3000)
            )
            val outputs = listOf(
                Stack(NaOH, 3),
                Stack(H2, 1000)
            )

            val sut = Recipe(inputs, outputs, 30, 30f)

            expect(sut.durationInTicks).toBe(600)
            expect(sut.energyPerRecipe).toBe(18000)
        }

        test("calculates input stream") {
            val inputs = listOf(
                Stack(Na, 1),
                Stack(H2O, 3000)
            )
            val outputs = listOf(
                Stack(NaOH, 3),
                Stack(H2, 1000)
            )
            val sut = Recipe(inputs, outputs, 30, 30f)

            val stream = sut.inputStream

            expect(stream.size).toBe(2)
            expect(stream[Na]).notToBeNull().toBeWithErrorTolerance(0.033f, 0.001f)
            expect(stream[H2O]).notToBeNull().toBeWithErrorTolerance(100f, 0.01f)
        }

        test("calculates output stream") {
            val inputs = listOf(
                Stack(Na, 1),
                Stack(H2O, 3000)
            )
            val outputs = listOf(
                Stack(NaOH, 3),
                Stack(H2, 1000)
            )
            val sut = Recipe(inputs, outputs, 30, 30f)

            val stream = sut.outputStream

            expect(stream.size).toBe(2)
            expect(stream[NaOH]).notToBeNull().toBeWithErrorTolerance(0.1f, 0.001f)
            expect(stream[H2]).notToBeNull().toBeWithErrorTolerance(33.33f, 0.01f)
        }
    }
})

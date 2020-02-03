package ru.ought.greg_recipe_balancer

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("LocalVariableName")
object MachineStackTest : Spek({
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

        test("has basic properties") {
            val sut = MachineStack(recipe)

            expect(sut.recipe).toBe(recipe)
            expect(sut.size).toBe(1)
            expect(sut.boundedRatio).toBe(1f)
        }

        test("has correct streams with size of 1") {
            val sut = MachineStack(recipe)

            val inputStream = sut.inputStream
            val outputStream = sut.outputStream

            expect(inputStream.size).toBe(2)
            expect(inputStream[Na]).notToBeNull().toBeWithErrorTolerance(0.033f, 0.001f)
            expect(inputStream[H2O]).notToBeNull().toBeWithErrorTolerance(100f, 1f)

            expect(outputStream.size).toBe(2)
            expect(outputStream[NaOH]).notToBeNull().toBeWithErrorTolerance(0.1f, 0.001f)
            expect(outputStream[H2]).notToBeNull().toBeWithErrorTolerance(33.33f, 1f)
        }

        test("has correct streams with size of 2") {
            val sut = MachineStack(recipe, 3)

            val inputStream = sut.inputStream
            expect(inputStream.size).toBe(2)
            expect(inputStream[Na]).notToBeNull().toBeWithErrorTolerance(0.1f, 0.001f)
            expect(inputStream[H2O]).notToBeNull().toBeWithErrorTolerance(300f, 1f)

            val outputStream = sut.outputStream
            expect(outputStream.size).toBe(2)
            expect(outputStream[NaOH]).notToBeNull().toBeWithErrorTolerance(0.3f, 0.001f)
            expect(outputStream[H2]).notToBeNull().toBeWithErrorTolerance(100f, 1f)
        }

        test("can be bounded by ratio") {
            val sut = MachineStack(recipe, 3, 0.666f)

            val inputStream = sut.inputStream
            expect(inputStream.size).toBe(2)
            expect(inputStream[Na]).notToBeNull().toBeWithErrorTolerance(0.066f, 0.001f)
            expect(inputStream[H2O]).notToBeNull().toBeWithErrorTolerance(200f, 1f)

            val outputStream = sut.outputStream
            expect(outputStream.size).toBe(2)
            expect(outputStream[NaOH]).notToBeNull().toBeWithErrorTolerance(0.2f, 0.001f)
            expect(outputStream[H2]).notToBeNull().toBeWithErrorTolerance(66f, 1f)
        }
    }
})

package ru.ought.greg_recipe_balancer

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("LocalVariableName")
object LineTest : Spek({
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
            val sut = Line(recipe)

            expect(sut.recipe).toBe(recipe)
            expect(sut.size).toBe(1)
        }

        test("has correct streams with size of 1") {
            val sut = Line(recipe)

            val inputStreams = sut.inputStream()
            val outputStreams = sut.outputStream()

            expect(inputStreams.size).toBe(2)
            expect(inputStreams[Na]).notToBeNull().toBeWithErrorTolerance(0.033f, 0.001f)
            expect(inputStreams[H2O]).notToBeNull().toBeWithErrorTolerance(100f, 0.01f)

            expect(outputStreams.size).toBe(2)
            expect(outputStreams[NaOH]).notToBeNull().toBeWithErrorTolerance(0.1f, 0.001f)
            expect(outputStreams[H2]).notToBeNull().toBeWithErrorTolerance(33.33f, 0.01f)
        }

        test("has correct streams with size of 2") {
            val sut = Line(recipe, 2)

            val inputStreams = sut.inputStream()
            val outputStreams = sut.outputStream()

            expect(inputStreams.size).toBe(2)
            expect(inputStreams[Na]).notToBeNull().toBeWithErrorTolerance(0.066f, 0.001f)
            expect(inputStreams[H2O]).notToBeNull().toBeWithErrorTolerance(200f, 0.01f)

            expect(outputStreams.size).toBe(2)
            expect(outputStreams[NaOH]).notToBeNull().toBeWithErrorTolerance(0.2f, 0.001f)
            expect(outputStreams[H2]).notToBeNull().toBeWithErrorTolerance(66.66f, 0.01f)
        }
    }
})

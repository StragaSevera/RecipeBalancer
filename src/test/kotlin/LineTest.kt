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

            val inputStream = sut.inputStream
            val outputStream = sut.outputStream

            expect(inputStream.size).toBe(2)
            expect(inputStream[Na]).notToBeNull().toBeWithErrorTolerance(0.033f, 0.001f)
            expect(inputStream[H2O]).notToBeNull().toBeWithErrorTolerance(100f, 0.01f)

            expect(outputStream.size).toBe(2)
            expect(outputStream[NaOH]).notToBeNull().toBeWithErrorTolerance(0.1f, 0.001f)
            expect(outputStream[H2]).notToBeNull().toBeWithErrorTolerance(33.33f, 0.01f)
        }

        test("has correct streams with size of 2") {
            val sut = Line(recipe, 3)

            val inputStream = sut.inputStream
            expect(inputStream.size).toBe(2)
            expect(inputStream[Na]).notToBeNull().toBeWithErrorTolerance(0.1f, 0.001f)
            expect(inputStream[H2O]).notToBeNull().toBeWithErrorTolerance(300f, 0.01f)

            val outputStream = sut.outputStream
            expect(outputStream.size).toBe(2)
            expect(outputStream[NaOH]).notToBeNull().toBeWithErrorTolerance(0.3f, 0.001f)
            expect(outputStream[H2]).notToBeNull().toBeWithErrorTolerance(100f, 0.01f)
        }

        test("can bound streams by input ingredient") {
            val lineNaOH = Line(recipe, 3)
            val Cl = Ingredient("Cl")
            val NaCl = Ingredient("NaCl")
            val recipeNa = Recipe(
                listOf(Stack(NaCl, 2)),
                listOf(Stack(Na, 1), Stack(Cl, 1000)),
                16, 30f
            )
            val lineNa = Line(recipeNa, 1)

            lineNaOH.boundInputBy(Na, lineNa)

            val inputStream = lineNaOH.boundedInputStream()
            expect(inputStream.size).toBe(2)
            expect(inputStream[Na]).notToBeNull().toBeWithErrorTolerance(0.062f, 0.001f)
            expect(inputStream[H2O]).notToBeNull().toBeWithErrorTolerance(187.5f, 0.01f)

            val outputStream = lineNaOH.boundedOutputStream()
            expect(outputStream.size).toBe(2)
            expect(outputStream[NaOH]).notToBeNull().toBeWithErrorTolerance(0.187f, 0.001f)
            expect(outputStream[H2]).notToBeNull().toBeWithErrorTolerance(62.5f, 0.01f)
        }

        test("stream is not bounded when input is enough") {
            val lineNaOH = Line(recipe, 3)
            val Cl = Ingredient("Cl")
            val NaCl = Ingredient("NaCl")
            val recipeNa = Recipe(
                listOf(Stack(NaCl, 2)),
                listOf(Stack(Na, 1), Stack(Cl, 1000)),
                16, 30f
            )
            val lineNa = Line(recipeNa, 2)

            lineNaOH.boundInputBy(Na, lineNa)

            val inputStream = lineNaOH.boundedInputStream()
            expect(inputStream.size).toBe(2)
            expect(inputStream[Na]).notToBeNull().toBeWithErrorTolerance(0.1f, 0.001f)
            expect(inputStream[H2O]).notToBeNull().toBeWithErrorTolerance(300f, 0.01f)

            val outputStream = lineNaOH.boundedOutputStream()
            expect(outputStream.size).toBe(2)
            expect(outputStream[NaOH]).notToBeNull().toBeWithErrorTolerance(0.3f, 0.001f)
            expect(outputStream[H2]).notToBeNull().toBeWithErrorTolerance(100f, 0.01f)
        }
    }
})

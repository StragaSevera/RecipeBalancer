package ru.ought.recipe_balancer

import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toBeWithErrorTolerance
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("LocalVariableName")
object MachineStackTest : Spek({
    describe("basic tests") {
        val a by memoized { Ingredient("A") }
        val b by memoized { Ingredient("B") }
        val c by memoized { Ingredient("C") }
        val d by memoized { Ingredient("D") }

        val recipe by memoized {
            Recipe(
                listOf(Stack(a, 1), Stack(b, 3000)),
                listOf(Stack(c, 3), Stack(d, 1000)),
                32f, 2.5f
            )
        }

        it("has basic properties") {
            val sut = MachineStack(recipe)

            expect(sut.recipe).toBe(recipe)
            expect(sut.size).toBe(1)
            expect(sut.boundedRatio).toBe(1f)
        }

        it("has correct streams with size of 1") {
            val sut = MachineStack(recipe)

            val inputStream = sut.inputStream
            expect(inputStream.size).toBe(2)
            expect(inputStream[a]).notToBeNull().toBeWithErrorTolerance(0.4f, 0.01f)
            expect(inputStream[b]).notToBeNull().toBeWithErrorTolerance(1200f, 1f)

            val outputStream = sut.outputStream
            expect(outputStream.size).toBe(2)
            expect(outputStream[c]).notToBeNull().toBeWithErrorTolerance(1.2f, 0.01f)
            expect(outputStream[d]).notToBeNull().toBeWithErrorTolerance(400f, 1f)
        }

        it("has correct streams with size of 2") {
            val sut = MachineStack(recipe, 2)

            val inputStream = sut.inputStream
            expect(inputStream.size).toBe(2)
            expect(inputStream[a]).notToBeNull().toBeWithErrorTolerance(0.8f, 0.01f)
            expect(inputStream[b]).notToBeNull().toBeWithErrorTolerance(2400f, 1f)

            val outputStream = sut.outputStream
            expect(outputStream.size).toBe(2)
            expect(outputStream[c]).notToBeNull().toBeWithErrorTolerance(2.4f, 0.01f)
            expect(outputStream[d]).notToBeNull().toBeWithErrorTolerance(800f, 1f)
        }

        it("can be bounded by ratio") {
            val sut = MachineStack(recipe, 3, 0.5f)

            val inputStream = sut.inputStream
            expect(inputStream.size).toBe(2)
            expect(inputStream[a]).notToBeNull().toBeWithErrorTolerance(0.6f, 0.01f)
            expect(inputStream[b]).notToBeNull().toBeWithErrorTolerance(1800f, 1f)

            val outputStream = sut.outputStream
            expect(outputStream.size).toBe(2)
            expect(outputStream[c]).notToBeNull().toBeWithErrorTolerance(1.8f, 0.01f)
            expect(outputStream[d]).notToBeNull().toBeWithErrorTolerance(600f, 1f)
        }
    }
})

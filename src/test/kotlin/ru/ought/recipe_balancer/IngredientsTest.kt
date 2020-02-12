package ru.ought.recipe_balancer

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


object IngredientsTest : Spek({
    describe("Ingredient") {
        it("has basic properties") {
            val sut = Ingredient("NaOH")
            expect(sut.name).toBe("NaOH")
        }
    }

    describe("Stack") {
        it("has basic properties") {
            val ingr = Ingredient("NaOH")
            val sut = Stack(ingr, 3)
            expect(sut.ingredient).toBe(ingr)
            expect(sut.amount).toBe(3)
        }
    }
})

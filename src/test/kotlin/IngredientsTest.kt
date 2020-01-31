package ru.ought.greg_recipe_balancer

import org.spekframework.spek2.Spek
import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect


object IngredientsTest : Spek({
    group("Ingredient") {
        test("has basic properties") {
            val sut = Ingredient("NaOH")
            expect(sut.name).toBe("NaOH")
        }
    }

    group("Stack") {
        test("has basic properties") {
            val ingr = Ingredient("NaOH")
            val sut = Stack(ingr, 3)
            expect(sut.ingredient).toBe(ingr)
            expect(sut.amount).toBe(3)
        }
    }
})

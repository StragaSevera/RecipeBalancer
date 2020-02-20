package ru.ought.recipe_balancer.managers

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import com.charleskorn.kaml.Yaml
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import ru.ought.recipe_balancer.Ingredient
import ru.ought.recipe_balancer.Recipe


object ManagerTest : Spek({
    describe("basic tests") {
        it("has basic serializing") {
            val m = Manager().apply {
                addIngredient("Cobblestone")
                addIngredient("Gravel")
                addIngredient("Sand")
                addIngredient("Glass")

                addMachine(
                    listOf(),
                    listOf(stackOf("Cobblestone", 1)),
                    32f, 0.8f, "Rock Breaker"
                )
                addMachine(
                    listOf(stackOf("Cobblestone", 1)),
                    listOf(stackOf("Gravel", 1)),
                    16f, 0.5f, "Hammer"
                )
            }

            val yaml = m.serializeYAML()
            print(yaml)
            val m2 = Manager.desearilizeYAML(yaml)
            expect(m2.ingredients).toBe(m.ingredients)
            expect(m2.machines).toBe(m.machines)
        }
    }
})

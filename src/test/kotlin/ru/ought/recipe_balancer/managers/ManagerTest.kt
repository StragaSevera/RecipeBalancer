package ru.ought.recipe_balancer.managers

import com.charleskorn.kaml.Yaml
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import ru.ought.recipe_balancer.Ingredient
import ru.ought.recipe_balancer.Recipe


object ManagerTest : Spek({
    describe("basic tests") {
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
        m2
    }
})

package ru.ought.recipe_balancer.managers

import com.charleskorn.kaml.Yaml
import ru.ought.recipe_balancer.Ingredient
import ru.ought.recipe_balancer.MachineStack
import ru.ought.recipe_balancer.Recipe
import ru.ought.recipe_balancer.Stack


class Manager(internal val ingredients: MutableList<Ingredient> = mutableListOf(),
              internal val machines: MutableList<MachineStack> = mutableListOf()
) {

    fun serializeYAML(): String {
        return Yaml.default.stringify(
            SerializableData.serializer(),
            SerializableData(this)
        )
    }

    companion object {
        fun desearilizeYAML(yaml: String): Manager {
            val data = Yaml.default.parse(SerializableData.serializer(), yaml)
            return Manager(data.ingredients, data.getMachines())
        }
    }

    fun addIngredient(name: String) {
        ingredients += Ingredient(name)
    }

    fun stackOf(name: String, amount: Int): Stack {
        return Stack(getIngredient(name), amount)
    }

    fun addMachine(
        inputs: List<Stack>,
        outputs: List<Stack>,
        energyPerTick: Float,
        duration: Float,
        machineName: String = "Common Machine",
        size: Int = 1, boundedRatio: Float = 1f
    ) {
        machines += MachineStack(Recipe(inputs, outputs, energyPerTick, duration, machineName), size, boundedRatio)
    }

    private fun getIngredient(name: String): Ingredient {
        return ingredients.first { it.name == name }
    }
}
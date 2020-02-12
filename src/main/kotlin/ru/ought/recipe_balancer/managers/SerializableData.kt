package ru.ought.recipe_balancer.managers

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.ought.recipe_balancer.Ingredient
import ru.ought.recipe_balancer.MachineStack
import ru.ought.recipe_balancer.Recipe
import ru.ought.recipe_balancer.Stack

@Serializable
class SerializableData(@Transient private val manager: Manager? = null) {
    val ingredients = manager?.ingredients ?: mutableListOf()
    private val machinesData = manager?.machines?.map(this::MachineStackData) ?: mutableListOf()
    fun getMachines(): MutableList<MachineStack> = machinesData.map { it.toMachineStack() }.toMutableList()

    private fun getIngredient(name: String): Ingredient {
        return ingredients.first { it.name == name }
    }

    @Serializable
    private inner class StackData(val ingredient: String, val amount: Int) {
        constructor(stack: Stack) : this(stack.ingredient.name, stack.amount)

        fun toStack(): Stack {
            return Stack(getIngredient(ingredient), amount)
        }
    }

    @Serializable
    private inner class RecipeData(
        val inputs: List<StackData>,
        val outputs: List<StackData>,
        val energyPerTick: Float,
        val duration: Float,
        val machineName: String = "Common Machine"
    ) {
        constructor(recipe: Recipe) : this(
            recipe.inputs.map { StackData(it) },
            recipe.outputs.map { StackData(it) },
            recipe.energyPerTick,
            recipe.duration,
            recipe.machineName
        )

        fun toRecipe(): Recipe = Recipe(
            inputs.map  {it.toStack()},
            outputs.map {it.toStack()},
            energyPerTick,
            duration,
            machineName
        )
    }

    @Serializable
    private inner class MachineStackData(val recipe: RecipeData, val size: Int = 1, val boundedRatio: Float = 1f) {
        constructor(machine: MachineStack) : this(RecipeData(machine.recipe), machine.size, machine.boundedRatio)

        fun toMachineStack(): MachineStack = MachineStack(recipe.toRecipe(), size, boundedRatio)
    }
}
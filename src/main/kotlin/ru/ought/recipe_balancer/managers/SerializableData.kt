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
    fun getMachines(): MutableList<MachineStack> = machinesData.map { it.toMachineStack(this) }.toMutableList()

    private fun getIngredient(name: String): Ingredient {
        return ingredients.first { it.name == name }
    }

    @Serializable
    inner class StackData(val ingredient: String, private val amount: Int) {
        constructor(stack: Stack) : this(stack.ingredient.name, stack.amount)

        fun toStack(data: SerializableData): Stack {
            return Stack(data.getIngredient(ingredient), amount)
        }
    }

    @Serializable
    inner class RecipeData(
        private val inputs: List<StackData>,
        private val outputs: List<StackData>,
        private val energyPerTick: Float,
        private val duration: Float,
        private val machineName: String = "Common Machine"
    ) {
        constructor(recipe: Recipe) : this(
            recipe.inputs.map { StackData(it) },
            recipe.outputs.map { StackData(it) },
            recipe.energyPerTick,
            recipe.duration,
            recipe.machineName
        )

        fun toRecipe(data: SerializableData): Recipe = Recipe(
            inputs.map  {it.toStack(data)},
            outputs.map {it.toStack(data)},
            energyPerTick,
            duration,
            machineName
        )
    }

    @Serializable
    inner class MachineStackData(private val id: Int, private val recipe: RecipeData, private val size: Int = 1, private val boundedRatio: Float = 1f) {
        constructor(machine: MachineStack) : this(machine.id, RecipeData(machine.recipe), machine.size, machine.boundedRatio)

        fun toMachineStack(data: SerializableData): MachineStack = MachineStack(id, recipe.toRecipe(data), size, boundedRatio)
    }

    class DataLink(val from: MachineStackData, val to: MachineStackData, val ingr: Ingredient)
}
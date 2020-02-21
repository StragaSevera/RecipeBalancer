package ru.ought.recipe_balancer.managers

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.ought.recipe_balancer.*

@Serializable
class SerializableData(@Transient private val manager: Manager? = null) {
    val ingredients = manager?.ingredients ?: mutableListOf()

    private val machinesData: List<MachineStackData> = manager?.machines?.map(this::MachineStackData) ?: mutableListOf()
    fun getMachines(): MutableList<MachineStack> = machinesData.map { it.toMachineStack(this) }.toMutableList()

    val dataLinks = manager?.dataLinks ?: mutableListOf()

    private fun getIngredient(name: String): Ingredient {
        return ingredients.first { it.name == name }
    }

    @Serializable
    private inner class StackData(val ingredient: String, private val amount: Int) {
        constructor(stack: Stack) : this(stack.ingredient.name, stack.amount)

        fun toStack(data: SerializableData): Stack {
            return Stack(data.getIngredient(ingredient), amount)
        }
    }

    @Serializable
    private inner class RecipeData(
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
    private inner class MachineStackData(private val id: String, private val recipe: RecipeData, private val size: Int = 1, private val boundedRatio: Float = 1f) {
        constructor(machine: MachineStack) : this(machine.id, RecipeData(machine.recipe), machine.size, machine.boundedRatio)

        fun toMachineStack(data: SerializableData): MachineStack = MachineStack(id, recipe.toRecipe(data), size, boundedRatio)
    }
}

@Serializable
data class DataLink(val from: String, val to: String, val ingr: Ingredient) {
    constructor(link: MachineLink): this(link.from.id, link.to.id, link.ingr)
}

package ru.ought.recipe_balancer.managers

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.ought.recipe_balancer.*

@Serializable
class SerializableData(@Transient private val manager: Manager? = null) {
    @kotlinx.serialization.Transient
    val ingredients = manager?.ingredients ?: mutableListOf()

    private val machinesData: List<MachineStackData> = manager?.machines?.map(this::MachineStackData) ?: mutableListOf()
    fun getMachines(): MutableList<MachineStack> = machinesData.map { it.toMachineStack(this) }.toMutableList()

    val dataLinks = manager?.dataLinks ?: mutableListOf()

    private fun getIngredient(name: String) =
        ingredients.find { it.name == name } ?: Ingredient(name).also { ingredients.add(it) }

    @Serializable
    private inner class StackData(val ingredient: String, private val amount: Int) {
        constructor(stack: Stack) : this(stack.ingredient.name, stack.amount)

        fun toStack(data: SerializableData): Stack {
            return Stack(data.getIngredient(ingredient), amount)
        }
    }

    @Serializable
    private inner class RecipeData(
        private val machineName: String = "Common Machine",
        private val inputs: List<StackData>,
        private val outputs: List<StackData>,
        private val duration: Float,
        private val energyPerTick: Float
    ) {
        constructor(recipe: Recipe) : this(
            recipe.machineName,
            recipe.inputs.map { StackData(it) },
            recipe.outputs.map { StackData(it) },
            recipe.duration,
            recipe.energyPerTick
        )

        fun toRecipe(data: SerializableData): Recipe = Recipe(
            inputs.map { it.toStack(data) },
            outputs.map { it.toStack(data) },
            energyPerTick,
            duration,
            machineName
        )
    }

    @Serializable
    private inner class MachineStackData(
        private val id: String,
        private val recipe: RecipeData,
        private val size: Int = 1
    ) {
        constructor(machine: MachineStack) : this(machine.id, RecipeData(machine.recipe), machine.size)

        fun toMachineStack(data: SerializableData): MachineStack = MachineStack(id, recipe.toRecipe(data), size)
    }
}

@Serializable
data class DataLink(val from: String, val to: String, val ingr: Ingredient) {
    constructor(link: MachineLink) : this(link.from.id, link.to.id, link.ingr)
}

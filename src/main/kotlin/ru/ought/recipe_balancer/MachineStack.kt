package ru.ought.recipe_balancer

class MachineStack(val recipe: Recipe, var size: Int = 1, var boundedRatio: Float = 1f) {
    //TODO: Decide if should implement hashCode()
    val inputStream get() = recipe.inputStream.transformStream()
    val outputStream get() = recipe.outputStream.transformStream()

    private fun IngredientStream.transformStream(): IngredientStream = mapValues { it.value * this@MachineStack.size * boundedRatio }

    override fun toString(): String {
        return "MachineStack(recipe=$recipe, size=$size, boundedRatio=$boundedRatio)"
    }
}
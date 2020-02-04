package ru.ought.recipe_balancer

class MachineStack(val recipe: Recipe, var size: Int = 1, var boundedRatio: Float = 1f) {
    val inputStream = recipe.inputStream.transformStream()
    val outputStream = recipe.outputStream.transformStream()

    private fun IngredientStream.transformStream(): IngredientStream = mapValues { it.value * this@MachineStack.size * boundedRatio }
}
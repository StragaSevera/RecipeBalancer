package ru.ought.greg_recipe_balancer

class Line(val recipe: Recipe, var size: Int = 1) {
    fun inputStream() = recipe.inputStream().parallelizeByMechs()
    fun outputStream() = recipe.outputStream().parallelizeByMechs()

    private fun IngredientStream.parallelizeByMechs(): IngredientStream = mapValues { it.value * this@Line.size }
}
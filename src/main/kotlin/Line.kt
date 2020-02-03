package ru.ought.greg_recipe_balancer

class Line(val recipe: Recipe, var size: Int = 1, var boundedRatio: Float = 1f) {
    val inputStream = recipe.inputStream.transformStream()
    val outputStream = recipe.outputStream.transformStream()

    private fun IngredientStream.transformStream(): IngredientStream = mapValues { it.value * this@Line.size * boundedRatio }
}
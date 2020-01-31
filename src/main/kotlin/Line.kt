package ru.ought.greg_recipe_balancer

import java.lang.Float.min

class Line(val recipe: Recipe, var size: Int = 1) {
    private val boundedInputs = mutableMapOf<Ingredient, Float>()

    val inputStream = recipe.inputStream.parallelizeByMechs()
    val outputStream = recipe.outputStream.parallelizeByMechs()

    private fun IngredientStream.parallelizeByMechs(): IngredientStream = mapValues { it.value * this@Line.size }

    fun boundInputBy(ingredient: Ingredient, line: Line) {
        require(inputStream.containsKey(ingredient))
        val ingredientFlow = checkNotNull(line.outputStream[ingredient])
        boundedInputs[ingredient] = ingredientFlow
    }

    fun boundedInputStream(): IngredientStream {
        val boundRatio = inputBoundRatio()
        return inputStream.mapValues { it.value * boundRatio }
    }

    fun boundedOutputStream(): IngredientStream {
        val boundRatio = inputBoundRatio()
        return outputStream.mapValues { it.value * boundRatio }
    }

    private fun inputBoundRatio(): Float {
        return min(boundedInputs.map {
            it.value / inputStream.getValue(it.key).toFloat()
        }.min() ?: 1f, 1f)
    }
}
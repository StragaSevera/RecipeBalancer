package ru.ought.greg_recipe_balancer

import java.lang.Float.min

class Line(val recipe: Recipe, var size: Int = 1) {
    private val boundedInputs = mutableMapOf<Ingredient, Line>()

    val inputStream = recipe.inputStream.parallelizeByMechs()
    val outputStream = recipe.outputStream.parallelizeByMechs()

    private fun IngredientStream.parallelizeByMechs(): IngredientStream = mapValues { it.value * this@Line.size }

    fun boundInputBy(ingredient: Ingredient, line: Line) {
        require(inputStream.containsKey(ingredient))
        require(line.outputStream.containsKey(ingredient))
        boundedInputs[ingredient] = line
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
        return min(boundedInputs.map { (ingr, line) ->
            line.boundedOutputStream().getValue(ingr) / inputStream.getValue(ingr).toFloat()
        }.min() ?: 1f, 1f)
    }
}
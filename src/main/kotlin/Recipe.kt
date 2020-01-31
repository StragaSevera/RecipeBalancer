package ru.ought.greg_recipe_balancer

import kotlin.math.roundToInt

typealias IngredientStream = Map<Ingredient, Float>

data class Recipe(val inputs: List<Stack>, val outputs: List<Stack>, val duration: Int, val energyPerTick: Float) {
    val durationInTicks get() = duration * 20

    val energyPerRecipe get() = (durationInTicks * energyPerTick).roundToInt()
    fun inputStream() = inputs.recipeStream()
    fun outputStream() = outputs.recipeStream()

    private fun List<Stack>.recipeStream(): IngredientStream = associate { it.ingredient to it.amount / duration.toFloat() }
}
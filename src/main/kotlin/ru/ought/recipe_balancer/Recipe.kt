package ru.ought.recipe_balancer

import kotlin.math.roundToInt

typealias IngredientStream = Map<Ingredient, Float>
typealias MutableIngredientStream = MutableMap<Ingredient, Float>

data class Recipe(
    val inputs: List<Stack>,
    val outputs: List<Stack>,
    val energyPerTick: Float,
    val duration: Float,
    val machineName: String = "Common Machine"
) {
    val durationInTicks = (duration * 20).roundToInt()

    val energyPerRecipe = (durationInTicks * energyPerTick).roundToInt()
    val inputStream = inputs.recipeStream()
    val outputStream = outputs.recipeStream()

    private fun List<Stack>.recipeStream(): IngredientStream = associate { it.ingredient to it.amount / duration }
}
package ru.ought.recipe_balancer

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
    val durationInTicks by lazy { (duration * 20).roundToInt() }
    val energyPerRecipe by lazy { (durationInTicks * energyPerTick).roundToInt() }
    val inputStream by lazy { inputs.recipeStream() }
    val outputStream by lazy { outputs.recipeStream() }

    private fun List<Stack>.recipeStream(): IngredientStream = associate { it.ingredient to it.amount / duration }
}
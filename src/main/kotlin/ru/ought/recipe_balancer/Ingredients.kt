package ru.ought.recipe_balancer

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(val name: String)

data class Stack(val ingredient: Ingredient, val amount: Int)

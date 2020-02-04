package ru.ought.recipe_balancer

data class Ingredient(val name: String)

data class Stack(val ingredient: Ingredient, val amount: Int)

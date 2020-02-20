package ru.ought.recipe_balancer

import java.util.concurrent.atomic.AtomicInteger

data class MachineStack(val id: Int, val recipe: Recipe, var size: Int = 1, var boundedRatio: Float = 1f) {
    constructor(recipe: Recipe, size: Int = 1, boundedRatio: Float = 1f): this(idGenerator.getAndIncrement(), recipe, size, boundedRatio)

    companion object {
        val idGenerator = AtomicInteger()
    }

    //TODO: Decide if should implement hashCode()
    val inputStream get() = recipe.inputStream.transformStream()
    val outputStream get() = recipe.outputStream.transformStream()

    private fun IngredientStream.transformStream(): IngredientStream = mapValues { it.value * this@MachineStack.size * boundedRatio }

    override fun toString(): String {
        return "MachineStack#$id(recipe=$recipe, size=$size, boundedRatio=$boundedRatio)"
    }
}
package ru.ought.recipe_balancer

import java.util.concurrent.atomic.AtomicInteger

data class MachineStack(val id: String, val recipe: Recipe, var size: Int = 1, var boundedRatio: Float = 1f) {
    constructor(id: Int, recipe: Recipe, size: Int = 1, boundedRatio: Float = 1f): this(getStringId(id), recipe, size, boundedRatio)
    constructor(recipe: Recipe, size: Int = 1, boundedRatio: Float = 1f): this(nextId.getAndIncrement(), recipe, size, boundedRatio)

    companion object {
        private const val ID_CHARS = "abcdefghijklmnopqrstuvwxyz"
        private fun getStringId(col: Int): String {
            if (col in ID_CHARS.indices) {
                return ID_CHARS[col].toString()
            }
            var div = col / ID_CHARS.length
            var mod = col % ID_CHARS.length
            if (mod == 0) {
                mod = ID_CHARS.length
                div--
            }
            return getStringId(div) + getStringId(mod)
        }

        private val nextId = AtomicInteger()
    }

    //TODO: Decide if should implement hashCode()
    val inputStream get() = recipe.inputStream.transformStream()
    val outputStream get() = recipe.outputStream.transformStream()

    override fun toString(): String = buildString {
        append("${recipe.machineName}#$id: $size, rate ${boundedPercent()}")
        append("\n  Input: ")
        for ((ingr, rate) in inputStream)
            append("${ingr.name}: $rate")
        append("\n  Output: ")
        for ((ingr, rate) in outputStream)
            append("${ingr.name}: $rate")
        append('\n')
    }
    private fun IngredientStream.transformStream(): IngredientStream = mapValues { it.value * this@MachineStack.size * boundedRatio }


    private fun boundedPercent(): String {
        return "%.2f%%".format(boundedRatio * 100f)
    }
}
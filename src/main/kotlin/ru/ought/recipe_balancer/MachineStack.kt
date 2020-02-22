package ru.ought.recipe_balancer

import java.util.concurrent.atomic.AtomicInteger

enum class BoundingStatus {
    OK, NOT_ENOUGH_INPUT, CANNOT_PUSH_OUTPUT
}

class MachineStack(val id: String, val recipe: Recipe, var size: Int = 1, var boundedRatio: Float = 1f) {
    var status = BoundingStatus.OK
    constructor(id: Int, recipe: Recipe, size: Int = 1, boundedRatio: Float = 1f) : this(
        getStringId(id),
        recipe,
        size,
        boundedRatio
    )

    constructor(recipe: Recipe, size: Int = 1, boundedRatio: Float = 1f) : this(
        nextId.getAndIncrement(),
        recipe,
        size,
        boundedRatio
    )

    companion object {
        private const val ID_CHARS = "abcdefghijklmnopqrstuvwxyz"
        private fun getStringId(id: Int): String {
            var charId = id
            return buildString {
                do {
                    val mod = charId % ID_CHARS.length
                    append(getChar(mod))
                    charId /= ID_CHARS.length
                } while (charId > 0)
            }.reversed()
        }

        private fun getChar(col: Int) = ID_CHARS[col].toString()

        private val nextId = AtomicInteger()
    }

    val inputStream get() = recipe.inputStream.transformStream()
    val outputStream get() = recipe.outputStream.transformStream()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MachineStack

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String = buildString {
        append("${recipe.machineName}#$id: $size, rate ${boundedPercent()}\n")
        append("  Input:  ")
        var isFirstLine = true
        for ((ingr, rate) in inputStream) {
            if(!isFirstLine) append("          ")
            append("${ingr.name}: ${"%.2f".format(rate)} ")
            append("(max ${"%.2f".format(getRate(recipe.inputStream, ingr))})\n")
            isFirstLine = false
        }
        if (inputStream.isEmpty()) append('\n')
        isFirstLine = true
        append("  Output: ")
        for ((ingr, rate) in outputStream) {
            if(!isFirstLine) append("          ")
            append("${ingr.name}: ${"%.2f".format(rate)} ")
            append("(max ${"%.2f".format(getRate(recipe.outputStream, ingr))})\n")
            isFirstLine = false
        }
        if (outputStream.isEmpty()) append('\n')
    }

    private fun getRate(
        ingredientStream: IngredientStream,
        ingr: Ingredient
    ) =
        ingredientStream.getValue(ingr) * size


    private fun IngredientStream.transformStream(): IngredientStream =
        mapValues { it.value * this@MachineStack.size * boundedRatio }

    private fun boundedPercent(): String {
        return "%.2f%%".format(boundedRatio * 100f)
    }
}
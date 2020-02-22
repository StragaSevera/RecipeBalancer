package ru.ought.recipe_balancer

import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

class MachineStack(
    val id: String,
    val recipe: Recipe,
    var size: Int = 1,
    var uuInput: Float = 0f,
    var uuOutput: Float = 0f
) {
    constructor(id: Int, recipe: Recipe, size: Int = 1, uuInput: Float = 0f, uuOutput: Float = 0f) :
            this(getStringId(id), recipe, size, uuInput, uuOutput)

    constructor(recipe: Recipe, size: Int = 1, uuInput: Float = 0f, uuOutput: Float = 0f) :
            this(nextId.getAndIncrement(), recipe, size, uuInput, uuOutput)

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


    val inputStream get() = recipe.inputStream.transformStream()
    val outputStream get() = recipe.outputStream.transformStream()

    override fun toString(): String = buildString {
        append("${id.toUpperCase()}: ${recipe.machineName}#$size, underusage ${boundedPercent(uuInput)} in ${boundedPercent(uuOutput)} out\n")
        append("     Input:  ")
        var isFirstLine = true
        for ((ingr, rate) in inputStream) {
            if (!isFirstLine) append("             ")
            append("${ingr.name}: ${"%.2f".format(rate)} ")
            append("(max ${"%.2f".format(getIdealRate(recipe.inputStream, ingr))})\n")
            isFirstLine = false
        }
        if (inputStream.isEmpty()) append('\n')
        isFirstLine = true
        append("     Output: ")
        for ((ingr, rate) in outputStream) {
            if (!isFirstLine) append("             ")
            append("${ingr.name}: ${"%.2f".format(rate)} ")
            append("(max ${"%.2f".format(getIdealRate(recipe.outputStream, ingr))})\n")
            isFirstLine = false
        }
        if (outputStream.isEmpty()) append('\n')
    }

    private fun getIdealRate(
        ingredientStream: IngredientStream,
        ingr: Ingredient
    ) =
        ingredientStream.getValue(ingr) * size


    private fun IngredientStream.transformStream(): IngredientStream =
        mapValues { it.value * this@MachineStack.size * boundRatio() }

    private fun boundRatio(): Float {
        check(uuInput in 0f..1f)
        check(uuOutput in 0f..1f)
        return (1f - uuInput) * (1f - uuOutput)
    }

    private fun boundedPercent(uu: Float): String {
        return "%.2f%%".format(uu * 100f)
    }
}
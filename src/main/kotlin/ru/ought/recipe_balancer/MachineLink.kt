package ru.ought.recipe_balancer

data class MachineLink(val from: MachineStack, val to: MachineStack, val ingr: Ingredient) {
    // TODO: Refactor two functions to use common functionality
    fun boundForward(): Boolean {
        val ratio = getBoundingRatio(to.inputStream, from.outputStream)
        val result = to.boundedRatio != ratio
        to.boundedRatio = ratio
        return result
    }

    fun boundBackward(): Boolean {
        val ratio = getBoundingRatio(from.outputStream, to.inputStream)
        val result = from.boundedRatio != ratio
        from.boundedRatio = ratio
        return result
    }

    private fun getBoundingRatio(
        boundingStream: IngredientStream,
        referenceStream: IngredientStream
    ): Float {
        val bounding = boundingStream[ingr]
        checkNotNull(bounding)
        val reference = referenceStream[ingr]
        checkNotNull(reference)
        return if (bounding <= reference) 1f
        else reference / bounding
    }
}
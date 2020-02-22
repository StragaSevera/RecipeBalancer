package ru.ought.recipe_balancer

enum class BoundingDirection {
    NONE, FORWARD, BACKWARD
}

data class MachineLink(val from: MachineStack, val to: MachineStack, val ingr: Ingredient) {
    var boundingDirection = BoundingDirection.NONE
    // TODO: Refactor two functions to use common functionality
    fun boundForward(): Boolean {
        val ratio = getBoundingRatio(to.inputStream, from.outputStream, to.boundedRatio)
        val result = to.boundedRatio != ratio
        to.boundedRatio = ratio
        if (result) {
            boundingDirection = BoundingDirection.FORWARD
            from.status = BoundingStatus.CANNOT_PUSH_OUTPUT
            to.status = BoundingStatus.NOT_ENOUGH_INPUT
        }
        return result
    }

    fun boundBackward(): Boolean {
        val ratio = getBoundingRatio(from.outputStream, to.inputStream, from.boundedRatio)
        val result = from.boundedRatio != ratio
        from.boundedRatio = ratio
        if (result) {
            boundingDirection = BoundingDirection.BACKWARD
            from.status = BoundingStatus.NOT_ENOUGH_INPUT
            to.status = BoundingStatus.CANNOT_PUSH_OUTPUT
        }
        return result
    }

    private fun getBoundingRatio(
        boundingStream: IngredientStream,
        referenceStream: IngredientStream,
        sourceRatio: Float
    ): Float {
        val bounding = boundingStream[ingr]
        checkNotNull(bounding)
        val reference = referenceStream[ingr]
        checkNotNull(reference)
        return if (bounding <= reference) sourceRatio
        else sourceRatio * reference / bounding
    }
}
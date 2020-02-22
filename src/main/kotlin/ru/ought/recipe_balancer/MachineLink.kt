package ru.ought.recipe_balancer

data class MachineLink(val from: MachineStack, val to: MachineStack, val ingr: Ingredient) {
    // TODO: Refactor two functions to use common functionality
    fun boundUUInput(): Boolean {
        val underusage = getUnderusage(to.inputStream, from.outputStream, to.uuInput)
        val result = to.uuInput != underusage
        to.uuInput = underusage
        return result
    }

    fun boundUUOutput(): Boolean {
        val underusage = getUnderusage(from.outputStream, to.inputStream, from.uuOutput)
        val result = from.uuOutput != underusage
        from.uuOutput = underusage
        return result
    }

    private fun getUnderusage(
        boundingStream: IngredientStream,
        referenceStream: IngredientStream,
        underusage: Float
    ): Float {
        val bounding = boundingStream[ingr]
        checkNotNull(bounding)
        val reference = referenceStream[ingr]
        checkNotNull(reference)
        return if (bounding <= reference) underusage
        else 1f - ((1f - underusage) * reference / bounding)
    }
}
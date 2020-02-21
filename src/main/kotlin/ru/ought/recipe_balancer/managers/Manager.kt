package ru.ought.recipe_balancer.managers

import com.charleskorn.kaml.Yaml
import ru.ought.recipe_balancer.*
import java.util.concurrent.atomic.AtomicInteger


class Manager(
    internal val ingredients: MutableList<Ingredient> = mutableListOf(),
    internal val machines: MutableList<MachineStack> = mutableListOf(),
    internal val dataLinks: MutableList<DataLink> = mutableListOf()
) {
    private val machineId = AtomicInteger()
    companion object {
        fun desearilizeYAML(yaml: String): Manager {
            val data = Yaml.default.parse(SerializableData.serializer(), yaml)
            return Manager(data.ingredients, data.getMachines(), data.dataLinks)
        }
    }

    fun serializeYAML(): String {
        return Yaml.default.stringify(
            SerializableData.serializer(),
            SerializableData(this)
        )
    }

    fun buildGraph(): MachineGraph {
        val graph = MachineGraph()
        for (link in dataLinks)
            graph.link(getMachine(link.from), getMachine(link.to), link.ingr)
        return graph
    }

    fun addIngredient(name: String) {
        ingredients += Ingredient(name)
    }

    fun stackOf(ingrName: String, amount: Int): Stack {
        return Stack(getIngredient(ingrName), amount)
    }

    fun addMachine(
        inputs: List<Stack>,
        outputs: List<Stack>,
        energyPerTick: Float,
        duration: Float,
        machineName: String = "Common Machine",
        size: Int = 1, boundedRatio: Float = 1f
    ) {
        machines += MachineStack(machineId.getAndIncrement(), Recipe(inputs, outputs, energyPerTick, duration, machineName), size, boundedRatio)
    }

    fun addLink(from: MachineStack, to: MachineStack, ingrName: String) {
        val ingr = getIngredient(ingrName)
        dataLinks += DataLink(from.id, to.id, ingr)
    }

    private fun getIngredient(name: String): Ingredient {
        return ingredients.first { it.name == name }
    }

    private fun getMachine(id: String) = machines.first { it.id == id }

}
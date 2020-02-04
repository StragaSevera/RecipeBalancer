package ru.ought.greg_recipe_balancer

data class MachineEdge(val from: MachineStack, val to: MachineStack, val by: Ingredient)

class MachineGraph {
    private val edges = mutableListOf<MachineEdge>()
}
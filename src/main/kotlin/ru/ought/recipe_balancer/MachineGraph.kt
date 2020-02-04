package ru.ought.recipe_balancer

class MachineGraph {
    private val _links: MutableMap<MachineStack, MutableList<MachineLink>> = mutableMapOf()
    val links: Map<MachineStack, List<MachineLink>> get() = _links
    private val _machines: MutableSet<MachineStack> = mutableSetOf()

    fun link(from: MachineStack, to: MachineStack, by: Ingredient) {
        val link = MachineLink(from, to, by)
        _links.getOrPut(from) { mutableListOf() }.add(link)
        _machines.add(from)
        _machines.add(to)
    }

    fun balanceForward() {
        val startingMachines = getStartingMachines()
        for (machine in startingMachines) {
            balanceForward(machine)
        }
    }

    private fun balanceForward(machine: MachineStack) {
        val stack = mutableListOf<MachineLink>()
        stack.pushAll(links[machine]?.reversed() ?: error("No links for a starting machine"))
        var current: MachineLink? = stack.pop()
        while (current != null) {
            current.boundForward()
            links[current.to]?.let { stack.pushAll(it.reversed()) }
            current = stack.pop()
        }
    }

    private fun getStartingMachines(): Iterable<MachineStack> {
        return _machines.filter { machine ->
            links.values.all { linkList ->
                linkList.all { it.to != machine }
            }
        }
    }

    private fun <T> MutableList<T>.push(item: T) = this.add(item)
    private fun <T> MutableList<T>.pushAll(items: Iterable<T>) = this.addAll(items)
    private fun <T> MutableList<T>.pop(): T? = if(this.count() > 0) this.removeAt(this.count() - 1) else null
    private fun <T> MutableList<T>.peek(): T? = if(this.count() > 0) this[this.count() - 1] else null
    private fun <T> MutableList<T>.hasMore() = this.count() > 0

}
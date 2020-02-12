package ru.ought.recipe_balancer

import kotlinx.serialization.Serializable

class MachineGraph {
    private val _forwardLinks: MutableMap<MachineStack, MutableList<MachineLink>> = mutableMapOf()
    val forwardLinks: Map<MachineStack, List<MachineLink>> get() = _forwardLinks

    private val _backwardLinks: MutableMap<MachineStack, MutableList<MachineLink>> = mutableMapOf()
    val backwardLinks: Map<MachineStack, List<MachineLink>> get() = _backwardLinks

    private val _machines: MutableSet<MachineStack> = mutableSetOf()

    fun link(from: MachineStack, to: MachineStack, by: Ingredient) {
        val link = MachineLink(from, to, by)
        _forwardLinks.getOrPut(from) { mutableListOf() }.add(link)
        _backwardLinks.getOrPut(to) { mutableListOf() }.add(link)
        _machines.add(from)
        _machines.add(to)
    }

    fun balanceForward(): Boolean {
        val machines = getStartingMachines()
        var wasChanged = false
        for (machine in machines) {
            wasChanged = wasChanged || balanceForward(machine)
        }
        return wasChanged
    }

    fun balanceBackward(): Boolean {
        val machines = getEndingMachines()
        var wasChanged = false
        for (machine in machines) {
            wasChanged = wasChanged || balanceBackward(machine)
        }
        return wasChanged
    }

    fun balance() {
        var wasChanged: Boolean
        do {
            wasChanged = false
            wasChanged = wasChanged || balanceForward()
            wasChanged = wasChanged || balanceBackward()
        } while (wasChanged)
    }

    private fun balanceForward(machine: MachineStack): Boolean {
        val stack = mutableListOf<MachineLink>()
        var wasChanged = false
        stack.pushAll(forwardLinks[machine]?.reversed() ?: error("No links for a root machine"))
        var current: MachineLink? = stack.pop()
        while (current != null) {
            wasChanged = wasChanged || current.boundForward()
            forwardLinks[current.to]?.let { stack.pushAll(it.reversed()) }
            current = stack.pop()
        }
        return wasChanged
    }

    private fun balanceBackward(machine: MachineStack): Boolean {
        val stack = mutableListOf<MachineLink>()
        var wasChanged = false
        stack.pushAll(backwardLinks[machine]?.reversed() ?: error("No links for a root machine"))
        var current: MachineLink? = stack.pop()
        while (current != null) {
            wasChanged = wasChanged || current.boundBackward()
            backwardLinks[current.from]?.let { stack.pushAll(it.reversed()) }
            current = stack.pop()
        }
        return wasChanged
    }

    private fun getStartingMachines(): Iterable<MachineStack> {
        return _machines.filter { machine ->
            forwardLinks.values.all { linkList ->
                linkList.all { it.to != machine }
            }
        }
    }

    private fun getEndingMachines(): Iterable<MachineStack> {
        return _machines.filter { machine ->
            forwardLinks.values.all { linkList ->
                linkList.all { it.from != machine }
            }
        }
    }

    private fun <T> MutableList<T>.push(item: T) = this.add(item)
    private fun <T> MutableList<T>.pushAll(items: Iterable<T>) = this.addAll(items)
    private fun <T> MutableList<T>.pop(): T? = if (this.count() > 0) this.removeAt(this.count() - 1) else null
    private fun <T> MutableList<T>.peek(): T? = if (this.count() > 0) this[this.count() - 1] else null
    private fun <T> MutableList<T>.hasMore() = this.count() > 0

}
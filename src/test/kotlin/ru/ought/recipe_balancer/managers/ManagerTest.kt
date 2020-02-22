package ru.ought.recipe_balancer.managers

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import ru.ought.recipe_balancer.MachineLink


object ManagerTest : Spek({
    describe("basic tests") {
        val manager by memoized { Manager().apply {
            addIngredient("Cobblestone")
            addIngredient("Gravel")
            addIngredient("Sand")

            addMachine(
                listOf(),
                listOf(stackOf("Cobblestone", 1)),
                32f, 0.8f, "Rock Breaker"
            )
            addMachine(
                listOf(stackOf("Cobblestone", 1)),
                listOf(stackOf("Gravel", 1)),
                16f, 0.5f, "Hammer"
            )
            addMachine(
                listOf(stackOf("Gravel", 1)),
                listOf(stackOf("Sand", 1)),
                16f, 0.5f, "Hammer"
            )

            addLink(machines[0], machines[1], "Cobblestone")
            addLink(machines[1], machines[2], "Gravel")
        } }

        it("can build correct graph") {
            val graph = manager.buildGraph()
            val links = graph.forwardLinks.flatMap { (_, v) -> v }
            expect(links).toBe(listOf(
                MachineLink(manager.machines[0], manager.machines[1], manager.ingredients[0]),
                MachineLink(manager.machines[1], manager.machines[2], manager.ingredients[1])
            ))
        }

        it("has basic serializing") {
            val yaml = manager.serializeYAML()
            val m2 = Manager.desearilizeYAML(yaml)
            expect(m2.ingredients).toBe(manager.ingredients)
            expect(m2.machines).toBe(manager.machines)
            expect(m2.dataLinks).toBe(manager.dataLinks)
        }
    }
})

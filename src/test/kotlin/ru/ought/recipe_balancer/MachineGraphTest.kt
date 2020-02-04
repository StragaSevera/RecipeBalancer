package ru.ought.recipe_balancer

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek


@Suppress("LocalVariableName")
object MachineGraphTest : Spek({
    group("basic tests") {
        val Na by memoized { Ingredient("Na") }
        val H2O by memoized { Ingredient("H2O") }
        val NaOH by memoized { Ingredient("NaOH") }
        val NaOHPile by memoized { Ingredient("Tiny Pile of NaOH") }
        val H2 by memoized { Ingredient("H2") }
        val pumpkin by memoized { Ingredient("Pumpkin") }
        val pumpkinSeed by memoized { Ingredient("Pumpkin Seed") }
        val seedOil by memoized { Ingredient("Seed Oil") }
        val CO by memoized { Ingredient("CO") }
        val methanol by memoized { Ingredient("Methanol") }
        val glycerol by memoized { Ingredient("Glycerol") }
        val bioDiesel by memoized { Ingredient("Bio Diesel") }

        val machineNaOH by memoized {
            MachineStack(
                Recipe(
                    listOf(Stack(Na, 1), Stack(H2O, 3000)),
                    listOf(Stack(NaOH, 3), Stack(H2, 1000)),
                    30f, 30f, "Chemical Reactor"
                ), 1
            )
        }
        val machineNaOHPile by memoized {
            MachineStack(
                Recipe(
                    listOf(Stack(NaOH, 1)),
                    listOf(Stack(NaOHPile, 9)),
                    4f, 5f, "Packager"
                ), 1
            )
        }
        val machinePumpkinSeed by memoized {
            MachineStack(
                Recipe(
                    listOf(Stack(pumpkin, 1)),
                    listOf(Stack(pumpkinSeed, 4)),
                    32f, 0.8f, "Packager"
                ), 1
            )
        }
        val machineSeedOil by memoized {
            MachineStack(
                Recipe(
                    listOf(Stack(pumpkinSeed, 1)),
                    listOf(Stack(seedOil, 10)),
                    2f, 1.6f, "Fluid Extractor"
                ), 1
            )
        }
        val machineMethanol by memoized {
            MachineStack(
                Recipe(
                    listOf(Stack(H2, 1000), Stack(CO, 500)),
                    listOf(Stack(methanol, 1500)),
                    120f, 7.5f, "Chemical Reactor"
                ), 1
            )
        }
        val machineBioDiesel by memoized {
            MachineStack(
                Recipe(
                    listOf(Stack(NaOHPile, 1), Stack(seedOil, 6000), Stack(methanol, 1000)),
                    listOf(Stack(glycerol, 1000), Stack(bioDiesel, 6000)),
                    30f, 30f, "Chemical Reactor"
                ), 1
            )
        }


        test("can be linked") {
            val sut = MachineGraph()

            sut.link(machinePumpkinSeed, machineSeedOil, pumpkinSeed)

            expect(sut.links).toBe(
                mapOf(
                    machinePumpkinSeed to
                            listOf(MachineLink(machinePumpkinSeed, machineSeedOil, pumpkinSeed))
                )
            )
        }
    }
})

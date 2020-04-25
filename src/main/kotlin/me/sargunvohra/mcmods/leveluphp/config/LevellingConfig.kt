package me.sargunvohra.mcmods.leveluphp.config

import kotlin.math.pow
import kotlin.math.roundToInt

data class LevellingConfig(
    val hpOffset: Int = 0,
    val maximumLevel: Int = 0,
    val hpPerLevel: Int = 0,
    val healOnLevelUp: Boolean = false,
    val resetOnDeath: Boolean = false,
    val xpTargetFunction: Function = Function(emptyList()),
    val xpPenaltyFunction: Function = Function(emptyList()),
    val levelPenaltyFunction: Function = Function(emptyList()),
    val primaryXpValues: PrimaryXpValues = PrimaryXpValues(0, 0),
    val overrides: Map<String, Int> = emptyMap()
) {
    fun validate() {
        check(hpOffset > -20)
        check(hpPerLevel >= 0)
        check(maximumLevel >= 0)

        xpTargetFunction.validate()
        xpPenaltyFunction.validate()
        levelPenaltyFunction.validate()

        primaryXpValues.validate()
        overrides.forEach { (entityId, xp) ->
            check(entityId.isNotEmpty())
            check(xp >= 0)
        }
    }

    data class Function(
        val terms: List<Term>
    ) {
        operator fun invoke(level: Int): Int {
            return terms.map { it(level) }.sum().roundToInt()
        }

        fun validate() = terms.forEach { it.validate() }

        override fun toString() = if (terms.isEmpty()) "0" else terms.joinToString("+")
    }

    data class Term(
        val coefficient: Double,
        val exponent: Double
    ) {
        operator fun invoke(level: Int) = coefficient * level.toDouble().pow(exponent)

        fun validate() {
            check(coefficient >= 0)
            check(exponent >= 0)
        }

        override fun toString() = "$coefficient*X^$exponent"
    }

    data class PrimaryXpValues(
        val animal: Int,
        val mob: Int
    ) {
        fun validate() {
            check(animal >= 0)
            check(mob >= 0)
        }

        override fun toString() = "{animal=$animal, mob=$mob}"
    }
}

package me.sargunvohra.leveluphp.data

data class LevellingConfig(
    val hpOffset: Int,
    val maximumLevel: Int,
    val hpPerLevel: Int,
    val healOnLevelUp: Boolean,
    val resetOnDeath: Boolean,
    val advancementScale: Scale,
    val deathPenaltyScale: Scale,
    val primaryXpValues: PrimaryXpValues,
    val overrides: Map<String, Int>
) {
    fun validate() {
        check(hpOffset > -20)
        check(hpPerLevel >= 1)
        check(maximumLevel >= 0)
        advancementScale.validate()
        deathPenaltyScale.validate()
        primaryXpValues.validate()
        overrides.forEach { entityId, xp ->
            check(entityId.isNotEmpty())
            check(xp >= 0)
        }
    }

    data class Scale(
        val base: Int,
        val scale: Int
    ) {
        fun validate() {
            check(base >= 0)
            check(scale >= if (base == 0) 1 else 0)
        }
    }

    data class PrimaryXpValues(
        val animal: Int,
        val mob: Int
    ) {
        fun validate() {
            check(animal >= 0)
            check(mob >= 0)
        }
    }
}

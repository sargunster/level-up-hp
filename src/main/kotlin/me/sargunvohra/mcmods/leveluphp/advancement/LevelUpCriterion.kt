package me.sargunvohra.mcmods.leveluphp.advancement

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import me.sargunvohra.mcmods.leveluphp.LevelUpHp
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import net.minecraft.advancement.PlayerAdvancementTracker
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.advancement.criterion.Criterion
import net.minecraft.server.network.ServerPlayerEntity
import java.lang.Exception
import java.util.function.Predicate

object LevelUpCriterion : Criterion<LevelUpCriterion.Conditions> {

    private val handlers = mutableMapOf<PlayerAdvancementTracker, Handler>()

    override fun getId() = LevelUpHp.id("player_levelled_up")

    override fun beginTrackingCondition(
        advancementTracker: PlayerAdvancementTracker,
        conditionsContainer: Criterion.ConditionsContainer<Conditions>
    ) {
        handlers
            .getOrPut(advancementTracker) { Handler(advancementTracker) }
            .add(conditionsContainer)
    }

    override fun endTrackingCondition(
        advancementTracker: PlayerAdvancementTracker,
        conditionsContainer: Criterion.ConditionsContainer<Conditions>
    ) {
        val handler = handlers[advancementTracker] ?: return
        handler.remove(conditionsContainer)
        if (handler.isEmpty())
            handlers.remove(advancementTracker)
    }

    override fun endTracking(advancementTracker: PlayerAdvancementTracker) {
        handlers.remove(advancementTracker)
    }

    override fun conditionsFromJson(
        jsonObject: JsonObject,
        deserializationContext: JsonDeserializationContext
    ): Conditions {
        return Conditions(
            LevelPredicate.deserialize(jsonObject.get("level"))
        )
    }

    fun handle(player: ServerPlayerEntity) {
        try {
            handlers[player.advancementManager]?.handle(player.hpLevelHandler)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    class Conditions(
        private val level: LevelPredicate
    ) : AbstractCriterionConditions(LevelUpCriterion.id),
        Predicate<HpLevelHandler> by level

    private class Handler(
        private val tracker: PlayerAdvancementTracker
    ) : MutableSet<Criterion.ConditionsContainer<Conditions>> by mutableSetOf() {

        fun handle(hpLevelHandler: HpLevelHandler) {
            this.filter { it.conditions.test(hpLevelHandler) }
                .forEach { it.apply(tracker) }
        }
    }

}

package me.sargunvohra.mcmods.leveluphp.advancement

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import me.sargunvohra.mcmods.leveluphp.LevelUpHp
import net.minecraft.advancement.PlayerAdvancementTracker
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.advancement.criterion.Criterion
import net.minecraft.server.network.ServerPlayerEntity

object LevelUpCriterion : Criterion<LevelUpCriterion.Conditions> {

    private val handlers = mutableMapOf<PlayerAdvancementTracker, Handler>()

    override fun getId() = LevelUpHp.id("player_levelled_up")

    override fun conditionsFromJson(
        jsonObject: JsonObject,
        deserializationContext: JsonDeserializationContext
    ): Conditions {
        return Conditions()
    }

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

    fun handle(player: ServerPlayerEntity) {
        handlers[player.advancementManager]?.handle()
    }

    class Conditions : AbstractCriterionConditions(LevelUpCriterion.id)

    private class Handler(
        private val tracker: PlayerAdvancementTracker
    ) : MutableSet<Criterion.ConditionsContainer<Conditions>> by mutableSetOf() {

        fun handle() {
            forEach {
                it.apply(tracker)
            }
        }
    }

}

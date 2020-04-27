package me.sargunvohra.mcmods.leveluphp.advancement

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import me.sargunvohra.mcmods.leveluphp.LuhpIds
import me.sargunvohra.mcmods.leveluphp.core.HpLeveller
import me.sargunvohra.mcmods.leveluphp.core.hpLeveller
import net.minecraft.advancements.ICriterionTrigger
import net.minecraft.advancements.PlayerAdvancements
import net.minecraft.advancements.criterion.CriterionInstance
import net.minecraft.entity.player.ServerPlayerEntity
import java.util.function.Predicate

class LevelUpCriterionTrigger : ICriterionTrigger<LevelUpCriterionTrigger.Conditions> {

    private val handlers = mutableMapOf<PlayerAdvancements, Handler>()

    override fun getId() = LuhpIds.LEVEL_UP_TRIGGER

    override fun addListener(
        advancements: PlayerAdvancements,
        listener: ICriterionTrigger.Listener<Conditions>
    ) {
        handlers
            .getOrPut(advancements) { Handler(advancements) }
            .add(listener)
    }

    override fun removeListener(
        advancements: PlayerAdvancements,
        listener: ICriterionTrigger.Listener<Conditions>
    ) {
        val handler = handlers[advancements] ?: return
        handler.remove(listener)
        if (handler.isEmpty())
            handlers.remove(advancements)
    }

    override fun removeAllListeners(advancementTracker: PlayerAdvancements) {
        handlers.remove(advancementTracker)
    }

    override fun deserializeInstance(
        jsonObject: JsonObject,
        deserializationContext: JsonDeserializationContext
    ): Conditions {
        return Conditions(
            LevelPredicate.deserialize(jsonObject.get("level"))
        )
    }

    fun test(player: ServerPlayerEntity) {
        try {
            handlers[player.advancements]?.handle(player.hpLeveller)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    class Conditions(
        private val level: LevelPredicate
    ) : CriterionInstance(LuhpIds.LEVEL_UP_TRIGGER),
        Predicate<HpLeveller> by level

    private class Handler(
        private val advancements: PlayerAdvancements
    ) : MutableSet<ICriterionTrigger.Listener<Conditions>> by mutableSetOf() {

        fun handle(hpLevelHandler: HpLeveller) {
            this.filter { it.criterionInstance.test(hpLevelHandler) }
                .forEach { it.grantCriterion(advancements) }
        }
    }

}

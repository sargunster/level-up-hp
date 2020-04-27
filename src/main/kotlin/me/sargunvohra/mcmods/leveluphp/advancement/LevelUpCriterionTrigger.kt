package me.sargunvohra.mcmods.leveluphp.advancement

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import me.sargunvohra.mcmods.leveluphp.LuhpIds
import me.sargunvohra.mcmods.leveluphp.core.HpLeveller
import me.sargunvohra.mcmods.leveluphp.core.hpLeveller
import net.minecraft.advancements.criterion.AbstractCriterionTrigger
import net.minecraft.advancements.criterion.CriterionInstance
import net.minecraft.entity.player.ServerPlayerEntity
import java.util.function.Predicate

class LevelUpCriterionTrigger : AbstractCriterionTrigger<LevelUpCriterionTrigger.Conditions>() {

    override fun getId() = LuhpIds.LEVEL_UP_TRIGGER

    override fun deserializeInstance(
        jsonObject: JsonObject,
        deserializationContext: JsonDeserializationContext
    ): Conditions {
        return Conditions(
            LevelPredicate.deserialize(jsonObject.get("level"))
        )
    }

    fun test(player: ServerPlayerEntity) {
        this.func_227070_a_(player.advancements) { conditions ->
            conditions.test(player.hpLeveller)
        }
    }

    class Conditions(
        private val level: LevelPredicate
    ) : CriterionInstance(LuhpIds.LEVEL_UP_TRIGGER),
        Predicate<HpLeveller> by level
}

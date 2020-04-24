package me.sargunvohra.mcmods.leveluphp.advancement

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import me.sargunvohra.mcmods.leveluphp.LuhpMod
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import net.minecraft.advancements.criterion.AbstractCriterionTrigger
import net.minecraft.advancements.criterion.CriterionInstance
import net.minecraft.entity.player.ServerPlayerEntity
import java.util.function.Predicate

object LevelUpCriterion : AbstractCriterionTrigger<LevelUpCriterion.Conditions>() {

    override fun getId() = LuhpMod.id("player_levelled_up")

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
            conditions.test(player.hpLevelHandler)
        }
    }

    class Conditions(
        private val level: LevelPredicate
    ) : CriterionInstance(LevelUpCriterion.id),
        Predicate<HpLevelHandler> by level

}

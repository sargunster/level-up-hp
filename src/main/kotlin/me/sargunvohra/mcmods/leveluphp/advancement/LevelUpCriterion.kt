package me.sargunvohra.mcmods.leveluphp.advancement

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import me.sargunvohra.mcmods.leveluphp.LevelUpHp
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.server.network.ServerPlayerEntity
import java.util.function.Predicate

object LevelUpCriterion : AbstractCriterion<LevelUpCriterion.Conditions>() {

    override fun getId() = LevelUpHp.id("player_levelled_up")

    override fun conditionsFromJson(
        jsonObject: JsonObject,
        deserializationContext: JsonDeserializationContext
    ): Conditions {
        return Conditions(
            LevelPredicate.deserialize(jsonObject.get("level"))
        )
    }

    fun test(player: ServerPlayerEntity) {
        this.test(player.advancementTracker) { conditions ->
            conditions.test(player.hpLevelHandler)
        }
    }

    class Conditions(
        private val level: LevelPredicate
    ) : AbstractCriterionConditions(LevelUpCriterion.id),
        Predicate<HpLevelHandler> by level

}

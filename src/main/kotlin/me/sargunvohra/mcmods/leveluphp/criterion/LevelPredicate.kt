package me.sargunvohra.mcmods.leveluphp.criterion

import com.google.gson.JsonElement
import me.sargunvohra.mcmods.leveluphp.capability.HpLevelHandler
import net.minecraft.advancements.criterion.MinMaxBounds
import net.minecraft.util.JSONUtils
import java.util.function.Predicate

class LevelPredicate(
    private val current: MinMaxBounds.IntBound,
    private val remaining: MinMaxBounds.IntBound
) : Predicate<HpLevelHandler> {

    override fun test(hpLevelHandler: HpLevelHandler): Boolean {
        val currentLevel = hpLevelHandler.level
        val remainingLevels = hpLevelHandler.config.maximumLevel - currentLevel
        return current.test(currentLevel) && remaining.test(remainingLevels)
    }

    companion object {
        fun deserialize(jsonElement: JsonElement?): LevelPredicate {
            if (jsonElement == null || jsonElement.isJsonNull)
                return LevelPredicate(MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED)
            return JSONUtils.getJsonObject(jsonElement, "level").let { obj ->
                LevelPredicate(
                    current = MinMaxBounds.IntBound.fromJson(obj.get("current")),
                    remaining = MinMaxBounds.IntBound.fromJson(obj.get("remaining"))
                )
            }
        }
    }
}
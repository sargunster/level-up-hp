package me.sargunvohra.mcmods.leveluphp.advancement

import com.google.gson.JsonElement
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import net.minecraft.util.JsonHelper
import net.minecraft.predicate.NumberRange
import java.util.function.Predicate

class LevelPredicate(
    private val current: NumberRange.IntRange,
    private val remaining: NumberRange.IntRange
) : Predicate<HpLevelHandler> {
    override fun test(hpLevelHandler: HpLevelHandler): Boolean {
        val currentLevel = hpLevelHandler.level
        val remainingLevels = hpLevelHandler.config.maximumLevel - currentLevel
        return current.test(currentLevel) && remaining.test(remainingLevels)
    }

    companion object {
        fun deserialize(jsonElement: JsonElement?): LevelPredicate {
            if (jsonElement == null || jsonElement.isJsonNull)
                return LevelPredicate(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY)
            return JsonHelper.asObject(jsonElement, "level").let { obj ->
                LevelPredicate(
                    current = NumberRange.IntRange.fromJson(obj.get("current")),
                    remaining = NumberRange.IntRange.fromJson(obj.get("remaining"))
                )
            }
        }
    }
}

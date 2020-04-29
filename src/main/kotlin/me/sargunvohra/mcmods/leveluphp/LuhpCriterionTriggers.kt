package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.advancement.LevelUpCriterionTrigger
import net.minecraft.advancements.CriteriaTriggers
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
object LuhpCriterionTriggers {
    val LEVEL_UP = LevelUpCriterionTrigger()

    init {
        CriteriaTriggers.register(LEVEL_UP)
    }
}

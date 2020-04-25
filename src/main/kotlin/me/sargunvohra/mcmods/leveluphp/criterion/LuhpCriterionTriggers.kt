package me.sargunvohra.mcmods.leveluphp.criterion

import net.minecraft.advancements.CriteriaTriggers
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
object LuhpCriterionTriggers {
    val LEVEL_UP = LevelUpCriterionTrigger()

    @SubscribeEvent
    fun register(event: FMLServerAboutToStartEvent) {
        CriteriaTriggers.register(LEVEL_UP)
    }
}

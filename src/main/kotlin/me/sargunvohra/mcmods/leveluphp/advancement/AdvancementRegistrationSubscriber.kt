package me.sargunvohra.mcmods.leveluphp.advancement

import net.minecraft.advancements.CriteriaTriggers
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
object AdvancementRegistrationSubscriber {
    val LEVEL_UP = LevelUpCriterionTrigger()

    @SubscribeEvent
    fun register(event: FMLServerAboutToStartEvent) {
        CriteriaTriggers.register(LEVEL_UP)
    }
}

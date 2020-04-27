package me.sargunvohra.mcmods.leveluphp.command

import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
object CommandRegistrationSubscriber {
    private val LEVEL_UP_HP = buildLevelUpHpCommand()

    @SubscribeEvent
    fun register(event: FMLServerStartingEvent) {
        event.commandDispatcher.register(LEVEL_UP_HP)
    }
}

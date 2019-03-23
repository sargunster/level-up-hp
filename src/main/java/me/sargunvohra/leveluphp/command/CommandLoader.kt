package me.sargunvohra.leveluphp.command

import me.sargunvohra.leveluphp.LevelUpHp
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent

@KotlinEventBusSubscriber(modid = LevelUpHp.MOD_ID)
object CommandLoader {
    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
        event.commandDispatcher.register(buildLevelUpHpCommand())
    }
}

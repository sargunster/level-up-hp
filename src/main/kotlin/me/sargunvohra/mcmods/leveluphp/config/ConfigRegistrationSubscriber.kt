package me.sargunvohra.mcmods.leveluphp.config

import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
object ConfigRegistrationSubscriber {

    @SubscribeEvent
    fun registerClientConfig(event: FMLClientSetupEvent) {
        ModLoadingContext.get().registerConfig(
            ModConfig.Type.CLIENT,
            ClientConfig.spec,
            "leveluphp.toml"
        )
    }

    @SubscribeEvent
    fun registerDataConfig(event: FMLServerAboutToStartEvent) {
        event.server.resourceManager.addReloadListener(LevellingConfigManager(event.server))
    }
}

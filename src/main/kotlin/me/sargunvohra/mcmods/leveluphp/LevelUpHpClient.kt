package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.network.SyncPacketConsumer
import me.sargunvohra.mcmods.leveluphp.config.ClientConfigManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.loader.api.FabricLoader
import terrails.healthoverlay.api.HealthRendererConfiguration

@Suppress("unused")
object LevelUpHpClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientConfigManager.init()

        ClientSidePacketRegistry.INSTANCE.register(
            SyncPacketConsumer.CHANNEL,
            SyncPacketConsumer()
        )
        if (FabricLoader.getInstance().isModLoaded("healthoverlay")) {
            HealthRendererConfiguration.GHOST_HEARTS = false
        }
    }
}

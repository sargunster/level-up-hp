package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.network.SyncPacketConsumer
import me.sargunvohra.mcmods.leveluphp.config.ClientConfigManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.loader.api.FabricLoader

@Suppress("unused")
object LevelUpHpClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientConfigManager.init()

        ClientSidePacketRegistry.INSTANCE.register(
            SyncPacketConsumer.CHANNEL,
            SyncPacketConsumer()
        )

        if (FabricLoader.getInstance().isModLoaded("healthoverlay")) {
            Class.forName("terrails.healthoverlay.api.HealthRendererConfiguration")
                .getField("GHOST_HEARTS")
                .set(null, false)
        }
    }
}

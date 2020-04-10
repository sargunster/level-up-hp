package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer
import me.sargunvohra.mcmods.leveluphp.config.ClientConfig
import me.sargunvohra.mcmods.leveluphp.network.SyncPacketConsumer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry

@Suppress("unused")
object LevelUpHpClient : ClientModInitializer {
    override fun onInitializeClient() {
        AutoConfig.register(ClientConfig::class.java) { def, cls ->
            Toml4jConfigSerializer(def, cls)
        }
        ClientSidePacketRegistry.INSTANCE.register(
            SyncPacketConsumer.CHANNEL,
            SyncPacketConsumer()
        )
    }
}

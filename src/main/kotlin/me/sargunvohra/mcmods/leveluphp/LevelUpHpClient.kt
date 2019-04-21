package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.autoconfig.api.AutoConfig
import me.sargunvohra.mcmods.autoconfig.api.serializer.JanksonConfigSerializer
import me.sargunvohra.mcmods.leveluphp.config.ClientConfig
import me.sargunvohra.mcmods.leveluphp.network.SyncPacketConsumer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry

@Suppress("unused")
object LevelUpHpClient : ClientModInitializer {
    override fun onInitializeClient() {
        AutoConfig.register("leveluphp", ClientConfig::class.java, ::JanksonConfigSerializer)
        ClientSidePacketRegistry.INSTANCE.register(
            SyncPacketConsumer.CHANNEL,
            SyncPacketConsumer()
        )
    }
}

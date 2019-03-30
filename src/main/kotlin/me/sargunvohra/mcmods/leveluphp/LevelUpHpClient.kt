package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.network.SyncPacketConsumer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry

object LevelUpHpClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(
            SyncPacketConsumer.CHANNEL,
            SyncPacketConsumer()
        )
    }
}

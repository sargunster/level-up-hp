package me.sargunvohra.mcmods.leveluphp.core

import me.sargunvohra.mcmods.leveluphp.LuhpIds
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.network.NetworkRegistry

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object HpLevellerSyncManager {
    private const val protocolVersion = "1"
    val CHANNEL = NetworkRegistry.newSimpleChannel(
        LuhpIds.LEVEL_SYNC_CHANNEL,
        { protocolVersion },
        { it == protocolVersion },
        { it == protocolVersion || it == "ABSENT" }
    )!!

    @SubscribeEvent
    fun registerMessages(event: FMLCommonSetupEvent) {
        @Suppress("INACCESSIBLE_TYPE")
        CHANNEL.registerMessage(
            0,
            HpLevellerSyncMessage::class.java,
            { message, packet -> HpLevellerSyncMessage.encode(message, packet) },
            { packet -> HpLevellerSyncMessage.decode(packet) },
            { message, contextSupplier -> message.consume(contextSupplier) }
        )
    }
}


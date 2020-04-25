package me.sargunvohra.mcmods.leveluphp.network

import me.sargunvohra.mcmods.leveluphp.LuhpIds
import net.minecraft.network.PacketBuffer
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkRegistry
import java.util.*

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object LuhpNetwork {
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
            PacketBuffer::class.java,
            { message, packetBuffer -> packetBuffer.writeBytes(message) },
            { packetBuffer -> packetBuffer.buffer },
            { message, context -> SyncPacketConsumer.accept(message, context.get()) },
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        )
    }
}

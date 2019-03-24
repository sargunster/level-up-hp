package me.sargunvohra.svlib.capability

import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.PacketDistributor
import net.minecraftforge.fml.network.simple.SimpleChannel
import java.util.function.Supplier

/**
 * This class is responsible for attaching PlayerCapabilities to players, and making sure the data
 * is always consistent, such as across dimensional travel or respawn, or from server to client.
 */
@Mod.EventBusSubscriber
open class PlayerCapabilityEventListener<Handler : PlayerCapability>(
    capSupplier: () -> Capability<Handler>,
    private val key: ResourceLocation,
    private val filter: (EntityPlayer) -> Boolean = { true },
    protocolVersion: String
) {

    private val channel: SimpleChannel = NetworkRegistry.newSimpleChannel(
        key, { protocolVersion }, { protocolVersion == it }, { protocolVersion == it }
    )

    private val capability by lazy { capSupplier() }

    init {
        @Suppress("INACCESSIBLE_TYPE", "LeakingThis")
        channel.registerMessage(
            0,
            PlayerCapabilityPacket::class.java,
            PlayerCapabilityPacket.Companion::encoder,
            PlayerCapabilityPacket.Companion::decoder,
            this::syncFromServer
        )
    }

    @SubscribeEvent
    fun onAttachCapabilities(event: AttachCapabilitiesEvent<Entity>) {
        val player = event.getObject() as? EntityPlayer ?: return
        if (!filter(player)) return

        val provider = PlayerCapabilityProvider(capability, key, this::syncToClient)
        event.addCapability(key, provider)
        provider.attach(player)
    }

    @SubscribeEvent
    fun onEntityJoinWorld(event: EntityJoinWorldEvent) {
        val player = event.entity as? EntityPlayerMP ?: return
        player.getCapability(capability).ifPresent {
            it.notifyModified()
            syncToClient(player, it)
        }
    }

    @SubscribeEvent
    fun onPlayerClone(event: PlayerEvent.Clone) {
        event
            .entityPlayer
            .getCapability(capability)
            .ifPresent {
                initializeClone(event.isWasDeath, event.entityPlayer, it)
            }
    }

    private fun initializeClone(wasDeath: Boolean, newPlayer: EntityPlayer, newHandler: Handler) {
        if (newHandler.shouldPersist(wasDeath)) {
            val entityData = newPlayer.entityData
            if (!entityData.contains(EntityPlayer.PERSISTED_NBT_TAG)) return

            val persistedData = entityData.get(EntityPlayer.PERSISTED_NBT_TAG) as NBTTagCompound
            val keyStr = key.toString()
            if (!persistedData.contains(keyStr)) return

            val data = persistedData.get(keyStr)
            val cap = capability
            cap.storage.readNBT(cap, newHandler, null, data)
        }
    }

    private fun syncToClient(player: EntityPlayerMP, handler: Handler) {
        if (player.connection == null) return // player doesn't have a client yet
        val cap = capability
        val nbt = cap.storage.writeNBT(cap, handler, null)!!
        val message = PlayerCapabilityPacket(nbt)
        channel.send(PacketDistributor.PLAYER.with { player }, message)
    }

    private fun syncFromServer(
        message: PlayerCapabilityPacket,
        contextSupplier: Supplier<NetworkEvent.Context>
    ) {
        val context = contextSupplier.get()
        if (context.direction != NetworkDirection.PLAY_TO_CLIENT) return
        context.packetHandled = true

        context.enqueueWork<Any> {
            val cap = this.capability
            Minecraft.getInstance()
                .player
                .getCapability(cap)
                .ifPresent { handler ->
                    cap.storage.readNBT(cap, handler, null, message.data)
                }
        }
    }
}

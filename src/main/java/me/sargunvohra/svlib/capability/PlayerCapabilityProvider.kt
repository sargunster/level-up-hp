package me.sargunvohra.svlib.capability

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional

/**
 * Specialized provider for capabilities that attach to players. Supports persisting data across
 * death or return from end.
 */
class PlayerCapabilityProvider<Handler : PlayerCapability>(
    capability: Capability<Handler>,
    private val key: ResourceLocation,
    private val syncToClient: (EntityPlayerMP, Handler) -> Unit
) : SerializableCapabilityProvider<Handler>(capability) {

    private var targetPlayer: EntityPlayer? = null

    init {
        val instance = instance
        instance.addListener { this.persistToEntityData() }
        instance.addListener { this.syncToClientIfMP() }
        instance.addListener { targetPlayer?.let { instance.apply(it) } }
    }

    fun attach(target: EntityPlayer) {
        this.targetPlayer = target
    }

    override fun <T> getCapability(capability: Capability<T>, side: EnumFacing?): LazyOptional<T> {
        val ret = super.getCapability(capability, side)
        ret.addListener { this.targetPlayer = null }
        return ret
    }

    private fun persistToEntityData() {
        val player = targetPlayer ?: return
        val data = capability.storage.writeNBT(capability, instance, null) ?: return

        val entityData = player.entityData
        if (!entityData.contains(EntityPlayer.PERSISTED_NBT_TAG))
            entityData.put(EntityPlayer.PERSISTED_NBT_TAG, NBTTagCompound())

        val persistedData = entityData.get(EntityPlayer.PERSISTED_NBT_TAG) as NBTTagCompound
        persistedData.put(key.toString(), data)
    }

    private fun syncToClientIfMP() {
        val player = targetPlayer as? EntityPlayerMP ?: return
        syncToClient(player, instance)
    }
}

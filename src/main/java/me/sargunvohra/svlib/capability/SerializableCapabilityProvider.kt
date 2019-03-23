package me.sargunvohra.svlib.capability

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable

/**
 * Variant of SimpleCapabilityProvider for capabilities that want to save data. If this is attached
 * to a player, the data will not persist across death or return from The End.
 */
open class SerializableCapabilityProvider<Handler>(
    capability: Capability<Handler>
) : SimpleCapabilityProvider<Handler>(capability), ICapabilitySerializable<NBTTagCompound> {

    override fun serializeNBT() =
        capability.storage.writeNBT(capability, instance, null) as NBTTagCompound

    override fun deserializeNBT(nbt: NBTTagCompound) =
        capability.storage.readNBT(capability, instance, null, nbt)
}

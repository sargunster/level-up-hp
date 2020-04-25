package me.sargunvohra.mcmods.leveluphp.capability

import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional

abstract class SimpleCapabilityProvider<T>(
    private val capability: Capability<T>
) : ICapabilitySerializable<INBT> {
    abstract val instance: T

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?) =
        capability.orEmpty(cap, LazyOptional.of { instance })

    override fun deserializeNBT(nbt: INBT?) {
        capability.readNBT(instance, null, nbt)
    }

    override fun serializeNBT(): INBT? {
        return capability.writeNBT(instance, null)
    }
}

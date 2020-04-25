package me.sargunvohra.mcmods.leveluphp.capability

import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional

abstract class SimpleCapabilityProvider<T>(
    private val capability: Capability<T>
) : ICapabilitySerializable<INBT> {
    abstract val implementation: T

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return if (cap == capability)
            LazyOptional.of { implementation }.cast()
        else LazyOptional.empty()
    }

    override fun deserializeNBT(nbt: INBT?) {
        capability.readNBT(implementation, null, nbt)
    }

    override fun serializeNBT(): INBT? {
        return capability.writeNBT(implementation, null)
    }
}

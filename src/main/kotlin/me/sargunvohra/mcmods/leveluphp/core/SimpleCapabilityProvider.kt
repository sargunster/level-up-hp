package me.sargunvohra.mcmods.leveluphp.core

import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional

abstract class SimpleCapabilityProvider<C>(
    private val capabilityType: Capability<C>
) : ICapabilitySerializable<INBT> {
    abstract val capability: C?

    override fun <CAP> getCapability(requested: Capability<CAP>, side: Direction?): LazyOptional<CAP> {
        return capability?.let { capabilityType.orEmpty(requested, LazyOptional.of { it }) }
            ?: LazyOptional.empty()
    }

    override fun deserializeNBT(nbt: INBT?) = capabilityType.readNBT(capability, null, nbt)

    override fun serializeNBT() = capabilityType.writeNBT(capability, null)
}

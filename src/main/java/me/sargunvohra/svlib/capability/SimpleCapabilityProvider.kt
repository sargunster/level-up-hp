package me.sargunvohra.svlib.capability

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional

/**
 * Simple provider for use with capabilities that simply attach without complex logic or
 * serialization.
 */
open class SimpleCapabilityProvider<Handler>(
    val capability: Capability<Handler>
) : ICapabilityProvider {

    val instance: Handler = capability.defaultInstance!!

    override fun <T> getCapability(capability: Capability<T>, side: EnumFacing?): LazyOptional<T> {
        if (capability !== this.capability)
            return LazyOptional.empty()

        @Suppress("UNCHECKED_CAST")
        return LazyOptional.of { this.instance } as LazyOptional<T>
    }
}

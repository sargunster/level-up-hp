package me.sargunvohra.leveluphp

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

object Capabilities {

    @JvmStatic
    @CapabilityInject(LuhpData::class)
    lateinit var LUHP_DATA: Capability<LuhpData>
}

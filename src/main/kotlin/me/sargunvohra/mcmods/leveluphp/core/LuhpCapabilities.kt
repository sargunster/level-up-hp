package me.sargunvohra.mcmods.leveluphp.core

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object LuhpCapabilities {
    @CapabilityInject(HpLeveller::class)
    lateinit var HP_LEVELLER_CAPABILITY: Capability<HpLeveller>

    @SubscribeEvent
    fun register(event: FMLCommonSetupEvent) {
        CapabilityManager.INSTANCE.register(
            HpLeveller::class.java,
            HpLeveller.Serializer,
            ::PlayerAttachedHpLeveller
        )
    }
}

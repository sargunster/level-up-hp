package me.sargunvohra.leveluphp.level

import me.sargunvohra.leveluphp.LevelUpHp
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

@KotlinEventBusSubscriber(modid = LevelUpHp.MOD_ID, bus = KotlinEventBusSubscriber.Bus.MOD)
object LevellerLoader {
    @JvmStatic
    @CapabilityInject(Leveller::class)
    lateinit var CAPABILITY: Capability<Leveller>

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onSetup(event: FMLCommonSetupEvent) {
        CapabilityManager.INSTANCE.register(
            Leveller::class.java,
            LevellerStorage()
        ) { Leveller() }
    }
}

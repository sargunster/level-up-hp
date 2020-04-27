package me.sargunvohra.mcmods.leveluphp

import net.minecraft.util.SoundEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object LuhpSoundEvents {
    val LEVEL_UP = SoundEvent(LuhpIds.LEVEL_UP_SOUND)

    @SubscribeEvent
    fun register(event: RegistryEvent.Register<SoundEvent>) {
        event.registry.register(LEVEL_UP.setRegistryName(LuhpIds.LEVEL_UP_SOUND))
    }
}

package me.sargunvohra.leveluphp.init

import me.sargunvohra.leveluphp.LevelUpHp
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.util.SoundEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

@KotlinEventBusSubscriber(modid = LevelUpHp.MOD_ID, bus = KotlinEventBusSubscriber.Bus.MOD)
object SoundEvents {

    val levelUp = LevelUpHp.res("levelup").let { SoundEvent(it).setRegistryName(it)!! }

    @SubscribeEvent
    fun onSoundEventRegister(event: RegistryEvent.Register<SoundEvent>) {
        event.registry.register(levelUp)
    }
}

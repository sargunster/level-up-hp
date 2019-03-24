package me.sargunvohra.leveluphp.init

import me.sargunvohra.leveluphp.LevelUpHp
import me.sargunvohra.leveluphp.item.HeartContainer
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

@Suppress("MemberVisibilityCanBePrivate")
@KotlinEventBusSubscriber(modid = LevelUpHp.MOD_ID, bus = KotlinEventBusSubscriber.Bus.MOD)
object Items {

    val heartContainer = HeartContainer()

    @SubscribeEvent
    fun onItemRegister(event: RegistryEvent.Register<Item>) {
        event.registry.register(heartContainer.setRegistryName(LevelUpHp.res("heart_container")))
    }
}

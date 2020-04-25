package me.sargunvohra.mcmods.leveluphp.item

import me.sargunvohra.mcmods.leveluphp.LuhpIds
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object LuhpItems {
    private val HEART_CONTAINER = HeartContainerItem()

    @SubscribeEvent
    fun register(event: RegistryEvent.Register<Item>) {
        event.registry.register(HEART_CONTAINER.setRegistryName(LuhpIds.HEART_CONTAINER_ITEM))
    }
}

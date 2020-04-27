package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.heartcontainer.HeartContainerItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object LuhpItems {
    private val HEART_CONTAINER = HeartContainerItem(
        Item.Properties().group(ItemGroup.MISC).maxStackSize(1)
    )

    @SubscribeEvent
    fun register(event: RegistryEvent.Register<Item>) {
        event.registry.register(HEART_CONTAINER.setRegistryName(LuhpIds.HEART_CONTAINER_ITEM))
    }
}

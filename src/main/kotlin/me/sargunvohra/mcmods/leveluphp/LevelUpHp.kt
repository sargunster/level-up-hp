package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.command.CommandLoader
import me.sargunvohra.mcmods.leveluphp.item.HeartContainerItem
import me.sargunvohra.mcmods.leveluphp.network.SyncPacketConsumer
import me.sargunvohra.mcmods.leveluphp.resource.ReloadListener
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.server.ServerStartCallback
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object LevelUpHp : ModInitializer, ClientModInitializer {

    val LEVEL_UP_SOUND = SoundEvent(id("levelup"))
    val RELOAD_LISTENER = ReloadListener()

    override fun onInitialize() {
        Registry.register(Registry.SOUND_EVENT, LEVEL_UP_SOUND.id, LEVEL_UP_SOUND)
        Registry.register(Registry.ITEM, id("heart_container"), HeartContainerItem())
        ServerStartCallback.EVENT.register(CommandLoader)
        ResourceManagerHelper.get(ResourceType.DATA).registerReloadListener(RELOAD_LISTENER)
    }

    override fun onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(SyncPacketConsumer.CHANNEL, SyncPacketConsumer())
    }

    fun id(name: String) = Identifier("leveluphp", name)
}

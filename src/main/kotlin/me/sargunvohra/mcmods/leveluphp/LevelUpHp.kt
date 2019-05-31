package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.command.CommandLoader
import me.sargunvohra.mcmods.leveluphp.item.HeartContainerItem
import me.sargunvohra.mcmods.leveluphp.config.ReloadListener
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.server.ServerStartCallback
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

@Suppress("unused")
object LevelUpHp : ModInitializer {

    val levelUpSound = SoundEvent(id("levelup"))
    val reloadListener = ReloadListener()

    override fun onInitialize() {
        Registry.register(Registry.SOUND_EVENT, id("levelup"), levelUpSound)
        Registry.register(Registry.ITEM, id("heart_container"), HeartContainerItem())
        ServerStartCallback.EVENT.register(CommandLoader)
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(reloadListener)
    }

    fun id(name: String) = Identifier("leveluphp", name)
}

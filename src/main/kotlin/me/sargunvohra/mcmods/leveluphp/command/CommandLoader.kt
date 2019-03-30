package me.sargunvohra.mcmods.leveluphp.command

import net.fabricmc.fabric.api.event.server.ServerStartCallback
import net.minecraft.server.MinecraftServer

object CommandLoader : ServerStartCallback {
    override fun onStartServer(server: MinecraftServer) {
        server.commandManager.dispatcher.register(buildLevelUpHpCommand())
    }
}

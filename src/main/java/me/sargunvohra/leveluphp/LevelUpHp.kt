package me.sargunvohra.leveluphp

import me.sargunvohra.leveluphp.command.RootCommand
import me.sargunvohra.leveluphp.core.PlayerLevelHandler
import me.sargunvohra.leveluphp.core.ProgressionController
import me.sargunvohra.leveluphp.gui.XpBarGuiController
import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent

/** Core mod class responsible for registering all the components that make the mod do its thing.  */
@Mod(LevelUpHp.MOD_ID)
object LevelUpHp {
    const val MOD_ID = BuildConfig.NAME

    init {
        FMLKotlinModLoadingContext.get().modEventBus.register(this)
        MinecraftForge.EVENT_BUS.register(this)

        PlayerLevelHandler.register()
        ProgressionController().register()
        XpBarGuiController().register()
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onSetup(ignored: FMLCommonSetupEvent) {
        PlayerLevelHandler.setup()
    }

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
        event.commandDispatcher.register(RootCommand.register())
    }
}

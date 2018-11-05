package me.sargunvohra.leveluphp

import me.sargunvohra.leveluphp.constants.MOD_ID
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory
import net.shadowfacts.shadowmc.config.GUIConfig

class ModGuiFactory : IModGuiFactory {

    override fun initialize(minecraftInstance: Minecraft) {}

    override fun hasConfigGui() = true

    override fun createConfigGui(parentScreen: GuiScreen) =
        GUIConfig(parentScreen, MOD_ID, ModConfig.config)

    override fun runtimeGuiCategories() = emptySet<IModGuiFactory.RuntimeOptionCategoryElement>()
}
